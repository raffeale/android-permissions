package com.stericson.permissions.donate.domain;

import java.util.ArrayList;
import java.util.List;


public class Result
{
	private boolean success = false;
	private String error;
    private String message;
	@SuppressWarnings("rawtypes")
	private List list = new ArrayList();

	
	@SuppressWarnings("unchecked")
	public void setList(List list)
	{
		this.list = null;
		this.list = list;
	}

	public void setSuccess(boolean success)
	{
		this.success = success;
	}

	public boolean isSuccess()
	{
		return success;
	}

	public void setError(String error)
	{
		this.error = error;
	}
	
	public String getError()
	{
		return error;
	}

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<?> getList()
	{
		return this.list;
	}

}
