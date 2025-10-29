package ma.dentalTech.common.exceptions;

public class ServiceException extends Exception {
    public ServiceException(String m) {
        super(m);
    }

    public ServiceException(String m, Throwable t) {
        super(m, t);
    }
}
