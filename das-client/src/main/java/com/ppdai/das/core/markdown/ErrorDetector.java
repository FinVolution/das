package com.ppdai.das.core.markdown;

public interface ErrorDetector {
	/**
	 * Collect the exception. 
	 * If the specified exception has been collected successfully
	 * return true, else return false.
	 * @param mark
	 * 		mark information
	 */
	void detect(ErrorContext mark);
}
