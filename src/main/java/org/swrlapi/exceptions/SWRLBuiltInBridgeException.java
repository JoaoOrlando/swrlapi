package org.swrlapi.exceptions;

public class SWRLBuiltInBridgeException extends SWRLAPIException
{
	private static final long serialVersionUID = 1L;

	public SWRLBuiltInBridgeException(String message)
	{
		super(message);
	}

	public SWRLBuiltInBridgeException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
