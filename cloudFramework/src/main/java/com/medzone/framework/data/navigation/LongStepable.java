package com.medzone.framework.data.navigation;

public class LongStepable implements Stepable<LongStepable> {

	protected static final long MAX_VALUE = Long.MAX_VALUE;

	private Long number;

	public LongStepable() {
		super();
	}

	public LongStepable(long number) {
		super();
		this.number = number;
	}

	public int compareTo(LongStepable another) {
		if (another == null) {
			return 1;
		}
		return number.compareTo(another.number);

	}

	public long getNumber() {
		return number;
	}

	public static long getMaxValue() {
		return MAX_VALUE;
	}

	public static long getMinValue() {
		return 0;
	}

	public static long getMaxValue(Paging<LongStepable> paging) {
		if (paging != null) {
			if (paging.getMax() != null) {
				return paging.getMax().getNumber();
			}
		}
		return getMaxValue();
	}

	public static long getMinValue(Paging<LongStepable> paging) {
		if (paging != null) {
			if (paging.getMin() != null) {
				return paging.getMin().getNumber();
			}
		}
		return getMinValue();
	}

	public void setNumber(long number) {
		this.number = number;
	}

	public LongStepable stepUp() {
		return new LongStepable(Math.min(MAX_VALUE - 1, number + 1));
	}

	public LongStepable stepDown() {
		return new LongStepable(Math.max(0, number - 1));
	}

}
