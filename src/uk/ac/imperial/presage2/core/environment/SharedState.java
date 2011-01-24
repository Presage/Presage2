package uk.ac.imperial.presage2.core.environment;

/**
 *  General shared state in the environment.
 */
class SharedState<T> {

    protected String type;

    protected T value;

    SharedState(String type, T value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public Class getValueType() {
        return value.getClass();
    }

}
