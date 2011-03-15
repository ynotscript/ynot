package ynot.util.breadcrum;

public class Progress implements Cloneable {

	private Integer currentStep;
	private Integer currentSubStep;
	private Integer nextStep;
	private Integer nextSubStep;

	private static final Integer INITIAL_STEP = 1;
	private static final Integer INITIAL_SUB_STEP = 1;

	public Progress() {
		currentStep = INITIAL_STEP;
		currentSubStep = INITIAL_SUB_STEP;
		nextStep = null;
		nextSubStep = null;
	}

	public final Integer getStep() {
		return currentStep;
	}

	public final Integer getSubStep() {
		return currentSubStep;
	}

	public final void goNextStep() {
		if (nextSubStep == null) {
			nextSubStep = INITIAL_SUB_STEP;
		}
		if (nextStep != null) {
			currentStep = nextStep;
			nextStep = null;
		} else {
			currentStep++;
		}
		goNextSubStep();
	}

	public final void goNextSubStep() {
		if (nextSubStep != null) {
			currentSubStep = nextSubStep;
			nextSubStep = null;
		} else {
			currentSubStep++;
		}
	}

	public final void setNext(Integer newNextStep) {
		setNext(newNextStep, INITIAL_SUB_STEP);
	}

	public final void setNext(final Integer newNextStep,
			final Integer newNextSubStep) {
		this.nextStep = newNextStep;
		this.nextSubStep = newNextSubStep;
	}

	public final boolean hasJumped() {
		return (nextStep != null || nextSubStep != null);
	}

	@Override
	public final Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public final String toString() {
		return "step:" + getStep() + ",subStep:" + getSubStep();
	}
}
