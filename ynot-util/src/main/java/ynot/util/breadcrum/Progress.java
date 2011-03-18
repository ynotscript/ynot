package ynot.util.breadcrum;

/**
 * To store the progression.
 * 
 * @author ERIC.QUESADA
 * 
 */
public class Progress implements Cloneable {

    /**
     * The current step.
     */
    private Integer currentStep;

    /**
     * The current sub step.
     */
    private Integer currentSubStep;

    /**
     * The next step.
     */
    private Integer nextStep;

    /**
     * The next subStep.
     */
    private Integer nextSubStep;

    /**
     * The initial step.
     */
    private static final Integer INITIAL_STEP = 1;

    /**
     * The initial subStep.
     */
    private static final Integer INITIAL_SUB_STEP = 1;

    /**
     * Default constructor.
     */
    public Progress() {
        currentStep = INITIAL_STEP;
        currentSubStep = INITIAL_SUB_STEP;
        nextStep = null;
        nextSubStep = null;
    }

    /**
     * To get the current step.
     * 
     * @return the current step.
     */
    public final Integer getStep() {
        return currentStep;
    }

    /**
     * To get the current subStep.
     * 
     * @return the current subStep.
     */
    public final Integer getSubStep() {
        return currentSubStep;
    }

    /**
     * To move to the next step and subStep.
     */
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

    /**
     * To go to the next subSTep.
     */
    public final void goNextSubStep() {
        if (nextSubStep != null) {
            currentSubStep = nextSubStep;
            nextSubStep = null;
        } else {
            currentSubStep++;
        }
    }

    /**
     * To set the next step.
     * 
     * @param newNextStep
     *            the new next step.
     */
    public final void setNext(final Integer newNextStep) {
        setNext(newNextStep, INITIAL_SUB_STEP);
    }

    /**
     * To set the next step and subStep.
     * 
     * @param newNextStep
     *            the new next step.
     * @param newNextSubStep
     *            the new next subStep.
     */
    public final void setNext(final Integer newNextStep,
            final Integer newNextSubStep) {
        this.nextStep = newNextStep;
        this.nextSubStep = newNextSubStep;
    }

    /**
     * To know if it moved.
     * 
     * @return true if it's the case else false.
     */
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
