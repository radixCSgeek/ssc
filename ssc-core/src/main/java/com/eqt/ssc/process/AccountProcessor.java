package com.eqt.ssc.process;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.AmazonServiceException;
import com.eqt.ssc.accounts.AccountManager;
import com.eqt.ssc.accounts.AccountManagerFactory;
import com.eqt.ssc.collector.APICollector;
import com.eqt.ssc.model.SSCAccountStatus;
import com.eqt.ssc.model.Token;
import com.eqt.ssc.state.StateEngine;
import com.eqt.ssc.util.Props;

public class AccountProcessor implements Callable<SSCAccountStatus> {
	private static final Log LOG = LogFactory.getLog(AccountProcessor.class);

	private List<APICollector> collectors = new ArrayList<APICollector>();
	private Token token;
	protected StateEngine state;
	protected AccountManager aman = null;

	/**
	 * calls the props class for ssc.process.api.collectors which will be a
	 * comma separated list of collectors to run.
	 */
	@SuppressWarnings("unchecked")
	public AccountProcessor(Token token, StateEngine state) {
		this.token = token;
		this.state = state;

		String prop = Props.getProp("ssc.process.api.collectors");
		if (prop == null || "".equals(prop))
			throw new IllegalStateException("ssc.process.api.collectors is not set");

		String[] collectors = prop.split(",");
		for (String c : collectors) {
			try {
				Class<? extends APICollector> clazz = (Class<? extends APICollector>) Class.forName(c);
				Constructor<?> cons = clazz.getConstructor(Token.class, StateEngine.class);
				APICollector newInstance = (APICollector) cons.newInstance(token, state);
				this.collectors.add(newInstance);
				LOG.info("added instance: " + clazz.getName());

			} catch (ClassNotFoundException e) {
				throw new IllegalStateException("could not find class", e);
			} catch (InstantiationException e) {
				throw new IllegalStateException("could not instantiate class", e);
			} catch (IllegalAccessException e) {
				throw new IllegalStateException("could not access class", e);
			} catch (NoSuchMethodException e) {
				throw new IllegalStateException("could not find constructor", e);
			} catch (SecurityException e) {
				throw new IllegalStateException("could not get through security", e);
			} catch (IllegalArgumentException e) {
				throw new IllegalStateException("could not get the right arguments", e);
			} catch (InvocationTargetException e) {
				throw new IllegalStateException("could not get the invocation target", e);
			}
		}
		
		aman = AccountManagerFactory.getInstance();
	}

	public SSCAccountStatus call() throws Exception {
		long start = System.currentTimeMillis();
		boolean ran = false;
		int total = 0;
		// TODO: handle exceptions and rerun up to a number of times.
		for (APICollector collector : collectors) {
			try {
				long curr = System.currentTimeMillis();
				if(token.lastUpdate(collector.getCollectorName()) + collector.getIntervalTime() < curr) {
					token.use(collector.getCollectorName());
					total += collector.collect();
					ran = true;
				}
			} catch (AmazonServiceException e) {
				//typically access denied, this is a big deal.
				LOG.error("unable to interact with " + collector.getCollectorName(), e);
				//TODO: write this error out to a store.
			} catch(Throwable t) {
				//TODO: write this error out to a store.
				LOG.error("boom, ungood, cannot run collector: " + collector.getCollectorName(),t);
				throw new Exception(t);
			}
		}
		long now = System.currentTimeMillis();
		
		LOG.info("###########################");
		
		//send an update back out to the AM for recording.
		if(ran) {
			LOG.info("calling addAccount");
			aman.addAccount(token); //--Russ: I think this is a bug because it won't ever try to run again if no Collectors ran this time
		}
		
		//TODO: more granular metrics reporting
		SSCAccountStatus status = new SSCAccountStatus(token);
		status.changes = total;
		status.success = true;
		status.timeSinceLastStart = start;
		status.totalCaptureTimeMS = now - start;
		
		return status;
	}

}
