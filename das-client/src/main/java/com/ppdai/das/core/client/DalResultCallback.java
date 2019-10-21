package com.ppdai.das.core.client;

public interface DalResultCallback {
	<T> void onResult(T result);
	void onError(Throwable e);
}
