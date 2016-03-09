package com.medzone.cloud.controller;

import com.medzone.cloud.cache.RuleCache;
import com.medzone.cloud.task.BaseGetControllerDataTask;
import com.medzone.framework.data.bean.imp.BaseMeasureData;
import com.medzone.framework.data.bean.imp.Rule;
import com.medzone.framework.data.navigation.LongStepable;

public class RuleController extends
		AbstractUsePagingTaskCacheController<Rule, LongStepable, RuleCache> {

	/**
	 * 
	 */
	private static RuleController ruleController;

	/**
	 * 
	 */
	private RuleController() {
	}

	public static RuleController getInstance() {
		if (ruleController == null) {
			ruleController = new RuleController();
		}
		return ruleController;
	}

	@Override
	protected LongStepable getStepable(Rule item) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected BaseGetControllerDataTask<Rule> createGetDataTask(
			Object... params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected RuleCache createCache() {
		// TODO Auto-generated method stub
		return new RuleCache();
	}

	@Override
	public boolean isVaild() {
		// TODO Auto-generated method stub
		return false;
	}

	public Rule getRulebyData(BaseMeasureData data) {
		return getCache().readRuleByData(data);

	}

}
