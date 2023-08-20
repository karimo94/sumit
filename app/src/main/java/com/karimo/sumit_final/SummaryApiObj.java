package com.karimo.sumit_final;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class SummaryApiObj
{
	public double time_taken;
	public String msg;
	public boolean ok;
	public int sentence_count;
	public String summary;
	ArrayList<Object> sentences = new ArrayList<Object>();
}
