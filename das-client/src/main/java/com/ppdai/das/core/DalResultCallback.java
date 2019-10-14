package com.ppdai.das.core;

public interface DalResultCallback {
	<T> void onResult(T result);
	void onError(Throwable e);
}
