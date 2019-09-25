package com.axiom.mobile.dao;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.axiom.mobile.model.Hardware;
import com.axiom.mobile.model.Mobile;
import com.axiom.mobile.model.Release;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Repository
public class MobileDAO {

	public List<Mobile> getMobiles(Map<String,String> allParams) throws Exception {
		List<Mobile> completeMobile = null;
		List<Mobile> searchMobile = null;
		try {
			completeMobile = getAllMobiles();
			if(allParams != null && allParams.size() > 0 ) {
				Predicate<Mobile> compositePredicate1 = constructMobilePredicate(allParams).stream().reduce(w ->true, Predicate::and);

				searchMobile = completeMobile.stream()
						.filter(compositePredicate1).collect(Collectors.toList());
			}
			else {
				return completeMobile;
			}

		} catch (Exception e) {
			throw e;
		}
		return searchMobile;
	}

	private List<Mobile> getAllMobiles() throws Exception{

		try (InputStreamReader reader = new InputStreamReader(new URL("https://api.myjson.com/bins/1f2r2v?pretty=true").openStream())) {
			Gson gson = new Gson();
			Type founderListType = new TypeToken<ArrayList<Mobile>>() {}.getType();
			return gson.fromJson(reader, founderListType);
		} catch (IOException e) {
			throw e;
		}
	}

	private List<Predicate<Mobile>> constructMobilePredicate(Map<String,String> allParams)throws Exception {
		List<Predicate<Mobile>> predicateList = new ArrayList<Predicate<Mobile>>();
		allParams.forEach((methodName, value) -> {
			Predicate<Mobile>  predicate= null;
			predicate = e -> {
				try {
					return validateInnerClassRef("get"+methodName, value,e);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				return false;
			};
			predicateList.add(predicate);

		});
		return predicateList;
	}

	private boolean validateInnerClassRef(String methodName,String value, Mobile e) throws Exception {

		List<String> classNames = new ArrayList<String>();
		classNames.add("com.axiom.mobile.model.Mobile");
		classNames.add("com.axiom.mobile.model.Hardware");
		classNames.add("com.axiom.mobile.model.Release");

		for (String className : classNames) {
			boolean flag = false;
			Class<?> cls;
			try {
				cls = Class.forName(className);

				Method[] methods = cls.getMethods(); 
				for (Method method : methods) {
					if(method.getName().equalsIgnoreCase(methodName))
					{
						if(cls.getCanonicalName().equalsIgnoreCase(Mobile.class.getCanonicalName())) {
							flag = method.getReturnType() == Integer.class ? method.invoke(e).equals(Integer.parseInt(value)) 
									: (method.getName().equalsIgnoreCase("getSim") ? String.valueOf(method.invoke(e)).toLowerCase().contains(value.toLowerCase())
											:method.invoke(e).equals(value));
							return flag == true;
						}
						else if(cls.getCanonicalName().equalsIgnoreCase(Hardware.class.getCanonicalName())) {
							Object hardware = e.getHardware();
							flag = method.invoke(hardware).equals(value);
							return flag == true;
						}
						else if(cls.getCanonicalName().equalsIgnoreCase(Release.class.getCanonicalName())) {
							Object release = e.getRelease();
							flag = method.getReturnType() == Double.class ? method.invoke(release).equals(Double.parseDouble(value)) : method.invoke(release).equals(value);
							return flag == true;
						}
					}
				}
			} catch (Exception e1) {
				e1.printStackTrace();
				throw e1;
			}
		}
		return false;
	}
}