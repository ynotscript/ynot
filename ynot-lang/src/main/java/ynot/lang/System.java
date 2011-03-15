package ynot.lang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * The system basics commands.
 * 
 * @author equesada
 */
public class System {

    // Member(s)

    /**
     * The logger.
     */
    private static Logger logger = Logger.getLogger(System.class);

    // Constructor(s)

    /**
     * The default constructor (java bean specification).
     */
    public System() {
    }

    // Getter(s)/Setter(s)

    /**
     * To get an object able to log everywhere.
     * 
     * @return a logger object
     */
    public final Logger getLogger() {
        return logger;
    }

    // Other functions

    /**
     * To read a line from the input (maybe the keyboard).
     * 
     * @param in
     *            the input stream to read
     * @return The read line, if there is problem so null
     */
    public final String readln(final InputStream in) {
        try {
            InputStreamReader converter = new InputStreamReader(in);
            BufferedReader reader = new BufferedReader(converter);
            return reader.readLine();
        } catch (IOException e) {
            getLogger().error("IOException", e);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return null;
    }

    /**
     * To get the current date at the desired format.
     * 
     * @param format
     *            the desired format.
     * @return the current date using the format.
     */
    public final String getDateTime(final String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        Date date = new Date();
        return dateFormat.format(date);
    }

    /**
     * To sleep a little bit :).
     * 
     * @param time
     *            the time to sleep (ms).
     */
    public final void sleep(final Integer time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            getLogger().error("InterruptedException", e);
        }
    }

    /**
     * To indicate if two objects are equals.
     * 
     * @param obj1
     *            the first object.
     * @param obj2
     *            the second object.
     * @return true is it's equals else false.
     */
    public final boolean equals(final Object obj1, final Object obj2) {
        if (obj1 == null && obj2 == null) {
            return true;
        } else if (obj1 == null) {
            return false;
        }
        return obj1.equals(obj2);
    }

    /**
     * To indicate if two objects are not equals.
     * 
     * @param obj1
     *            the first object.
     * @param obj2
     *            the second object.
     * @return true is it's not equals else false.
     */
    public final boolean notEquals(final Object obj1, final Object obj2) {
        return !equals(obj1, obj2);
    }

    /**
     * To indicate if obj1 is less obj2.
     * 
     * @param obj1
     *            the first object.
     * @param obj2
     *            the second object.
     * @return true is obj1 is less obj2.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public final boolean lessThan(final Object obj1, final Object obj2) {
        if (obj1 instanceof Number && obj2 instanceof Number) {
            return (((Number) obj1).doubleValue() < ((Number) obj2)
                    .doubleValue());
        }
        if (obj1 instanceof Comparable) {
            return (((Comparable) obj1).compareTo(obj2) < 0);
        }
        return false;
    }

    /**
     * To indicate if obj1 is high obj2.
     * 
     * @param obj1
     *            the first object.
     * @param obj2
     *            the second object.
     * @return true is obj1 is greater obj2.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public final boolean greaterThan(final Object obj1, final Object obj2) {
        if (obj1 instanceof Number && obj2 instanceof Number) {
            return (((Number) obj1).doubleValue() > ((Number) obj2)
                    .doubleValue());
        }
        if (obj1 instanceof Comparable) {
            return (((Comparable) obj1).compareTo(obj2) > 0);
        }
        return false;
    }

    /**
     * To indicate if obj1 is less or equals obj2.
     * 
     * @param obj1
     *            the first object.
     * @param obj2
     *            the second object.
     * @return true is obj1 is less or equals obj2.
     */
    public final boolean lessOrEquals(final Object obj1, final Object obj2) {
        return (equals(obj1, obj2) || lessThan(obj1, obj2));
    }

    /**
     * To indicate if obj1 is high or equals obj2.
     * 
     * @param obj1
     *            the first object.
     * @param obj2
     *            the second object.
     * @return true is obj1 is greater or equals obj2.
     */
    public final boolean greaterOrEquals(final Object obj1, final Object obj2) {
        return (equals(obj1, obj2) || greaterThan(obj1, obj2));
    }

    /**
     * To make a sum.
     * 
     * @param obj1
     *            the first integer.
     * @param obj2
     *            the second integer.
     * @return the sum.
     */
    public final Number sum(final Number obj1, final Number obj2) {
        if (obj1 instanceof Integer && obj2 instanceof Integer) {
            return obj1.intValue() + obj2.intValue();
        } else {
            return obj1.doubleValue() + obj2.doubleValue();
        }
    }

    /**
     * To make a subtraction.
     * 
     * @param obj1
     *            the first integer.
     * @param obj2
     *            the second integer.
     * @return the subtraction.
     */
    public final Number sub(final Number obj1, final Number obj2) {
        if (obj1 instanceof Integer && obj2 instanceof Integer) {
            return obj1.intValue() - obj2.intValue();
        } else {
            return obj1.doubleValue() - obj2.doubleValue();
        }
    }

    /**
     * To multiply numbers.
     * @param obj1 first number.
     * @param obj2 second number.
     * @return the result.
     */
    public final Number multiply(final Number obj1, final Number obj2) {
        if (obj1 instanceof Integer && obj2 instanceof Integer) {
            return obj1.intValue() * obj2.intValue();
        } else {
            return obj1.doubleValue() * obj2.doubleValue();
        }
    }

    /**
     * To divide numbers.
     * @param obj1 first number.
     * @param obj2 second number.
     * @return the result.
     */
    public final Number divide(final Number obj1, final Number obj2) {
        if (obj1 instanceof Integer && obj2 instanceof Integer) {
            return obj1.intValue() / obj2.intValue();
        } else {
            return obj1.doubleValue() / obj2.doubleValue();
        }
    }

    /**
     * To modulo numbers.
     * @param obj1 first number.
     * @param obj2 second number.
     * @return the result.
     */
    public final Number modulo(final Number obj1, final Number obj2) {
        if (obj1 instanceof Integer && obj2 instanceof Integer) {
            return obj1.intValue() % obj2.intValue();
        } else {
            return obj1.doubleValue() % obj2.doubleValue();
        }
    }

    /**
     * To power numbers.
     * @param obj1 first number.
     * @param obj2 second number.
     * @return the result.
     */
    public final Number power(final Number obj1, final Number obj2) {
        if (obj1 instanceof Integer && obj2 instanceof Integer) {
            return (int) Math.pow(obj1.intValue(), obj2.intValue());
        } else {
            return Math.pow(obj1.doubleValue(), obj2.doubleValue());
        }
    }

    /**
     * To set a value.
     * 
     * @param obj
     *            the value to set.
     * @return the given object.
     */
    public final Object assign(final Object obj) {
        return obj;
    }

    /**
     * To make the concatenation of objects.
     * 
     * @param list
     *            the list of object.
     * @return the concatenation.
     */
    public final String concat(final List<Object> list) {
        StringBuilder sb = new StringBuilder();
        for (Object obj : list) {
            if (obj == null) {
                sb.append("null");
            } else {
                sb.append(obj.toString());
            }
        }
        return sb.toString();
    }

    /**
     * To terminate the application.
     * 
     * @param status
     *            the status to send.
     */
    public final void exit(final Integer status) {
        java.lang.System.exit(status);
    }

}
