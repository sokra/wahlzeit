package org.wahlzeit.model;

public class ReadWriteException extends RuntimeException {

	private static final long serialVersionUID = 796018674019464408L;
	
	public ReadWriteException(Throwable e) {
		super(e);
	}

}
