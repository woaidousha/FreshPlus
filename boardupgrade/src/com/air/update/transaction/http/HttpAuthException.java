/* ---------------------------------------------------------------------------------------------
 *
 *            Capital Alliance Software Confidential Proprietary
 *            (c) Copyright CAS 201{x}, All Rights Reserved
 *                          www.pekall.com
 *
 * ----------------------------------------------------------------------------------------------
 */
package com.air.update.transaction.http;

/**
 * NOT AUTHORIZED, HTTP CODE 401
 *
 * @author wjl
 */
public class HttpAuthException extends HttpRefusedException {

    private static final long serialVersionUID = 4206324519931063593L;

    public HttpAuthException(Exception cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    public HttpAuthException(String msg, Exception cause, int statusCode) {
        super(msg, cause, statusCode);
        // TODO Auto-generated constructor stub
    }

    public HttpAuthException(String msg, Exception cause) {
        super(msg, cause);
        // TODO Auto-generated constructor stub
    }

    public HttpAuthException(String msg, int statusCode) {
        super(msg, statusCode);
        // TODO Auto-generated constructor stub
    }

    public HttpAuthException(String msg) {
        super(msg);
        // TODO Auto-generated constructor stub
    }
}
