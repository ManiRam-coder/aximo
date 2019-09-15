package com.axiom.mobile.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.axiom.mobile.dao.MobileDAO;
import com.axiom.mobile.model.Mobile;

@RestController
@RequestMapping(path = "/mobile")
public class MobileController 
{
	@Autowired
	private MobileDAO employeeDao;

	@GetMapping(path="/search", produces = "application/json")
	public List<Mobile> getMobileHandset(@RequestParam(required = false) Map<String,String> allParams) throws Exception 
	{

		return employeeDao.getMobiles(allParams);

	}
}
