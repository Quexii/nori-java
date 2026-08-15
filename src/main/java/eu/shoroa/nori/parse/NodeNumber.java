package eu.shoroa.nori.parse;

public class NodeNumber {
    public final NumberType type;
    public final int intValue;
    public final long longValue;
    public final double doubleValue;
    public final float floatValue;

    public NodeNumber(NumberType type, int intValue, long longValue, double doubleValue, float floatValue) {
        this.type = type;
        this.intValue = intValue;
        this.longValue = longValue;
        this.doubleValue = doubleValue;
        this.floatValue = floatValue;
    }

    public static NodeNumber ofInt(int value) {
        return new NodeNumber(NumberType.INT, value, 0, 0, 0);
    }

    public static NodeNumber ofLong(long value) {
        return new NodeNumber(NumberType.LONG, 0, value, 0, 0);
    }

    public static NodeNumber ofHex(int value) {
        return new NodeNumber(NumberType.HEX, value, 0, 0, 0);
    }

    public static NodeNumber ofBinary(int value) {
        return new NodeNumber(NumberType.BINARY, value, 0, 0, 0);
    }

    public static NodeNumber ofOctal(int value) {
        return new NodeNumber(NumberType.OCTAL, value, 0, 0, 0);
    }

    public static NodeNumber ofDouble(double value) {
        return new NodeNumber(NumberType.DOUBLE, 0, 0, value, 0);
    }

    public static NodeNumber ofFloat(float value) {
        return new NodeNumber(NumberType.FLOAT, 0, 0, 0, value);
    }

    public int getInt() {
        switch (type) {
            case HEX:
            case OCTAL:
            case BINARY:
            case INT:
                return intValue;
            case LONG:
                return (int) longValue;
            case FLOAT:
                return (int) floatValue;
            case DOUBLE:
                return (int) doubleValue;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    public long getLong() {
        switch (type) {
            case HEX:
            case OCTAL:
            case BINARY:
            case INT:
                return intValue;
            case LONG:
                return longValue;
            case FLOAT:
                return (long) floatValue;
            case DOUBLE:
                return (long) doubleValue;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    public double getDouble() {
        switch (type) {
            case HEX:
            case OCTAL:
            case BINARY:
            case INT:
                return intValue;
            case LONG:
                return longValue;
            case FLOAT:
                return floatValue;
            case DOUBLE:
                return doubleValue;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    public float getFloat() {
        switch (type) {
            case HEX:
            case OCTAL:
            case BINARY:
            case INT:
                return intValue;
            case LONG:
                return longValue;
            case FLOAT:
                return floatValue;
            case DOUBLE:
                return (float) doubleValue;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }
}
