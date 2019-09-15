package com.axiom.mobile.dao;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.axiom.mobile.model.Mobile;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Repository
public class MobileDAO {


	public List<Mobile> getMobiles(Map<String,String> allParams) throws Exception {

		URL url;
		List<Mobile> completeMobile = null;
		List<Mobile> searchMobile = null;
		try {
			url = new URL("https://api.myjson.com/bins/1f2r2v?pretty=true");
			InputStreamReader reader = new InputStreamReader(url.openStream());
			Gson gson = new Gson();
			Type founderListType = new TypeToken<ArrayList<Mobile>>() {
			}.getType();
			completeMobile = gson.fromJson(reader, founderListType);

			/*	List<Predicate<Mobile>> allPredicatesTest = null;
			allPredicatesTest = new ArrayList<Predicate<Mobile>>();
			Predicate<Mobile> batteryPredicate = e -> e.getHardware().getBattery()
					.equals("Li-Po 7812 mAh battery (29.45 Wh)");
			String st = "Brand()";
			Predicate<Mobile> brandPredicate = e -> e.getBrand().equals("Apple");
			allPredicatesTest.add(brandPredicate);
			allPredicatesTest.add(batteryPredicate);*/
			//		allPredicatesTest =  constructMobilePredicate(allParams);
			if(allParams != null && allParams.size() > 0 ) {
				Predicate<Mobile> compositePredicate1 = constructMobilePredicate(allParams).stream().reduce(w ->true, Predicate::and);

				searchMobile = completeMobile.stream()
						.filter(compositePredicate1).collect(Collectors.toList());
			}
			else {
				return  completeMobile;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw e;
		}
		return searchMobile;
	}

	/*private List<Predicate<Mobile>> constructMobilePredicate(Map<String,String> allParams) throws ClassNotFoundException {
		Class<?> cls;
		List<Predicate<Mobile>> predicateList = new ArrayList<Predicate<Mobile>>();
		cls = Class.forName(Mobile.class.getName());
		allParams.forEach((methodName, value) -> {
			final String methodNameFinal = "get"+methodName;
			Method[] methods = cls.getMethods(); 
			try {
				final Object t = cls.newInstance();

				Predicate<Mobile>  first= null;

				first= e1 -> {
					try {
						for (Method method : methods) {
							if(methodNameFinal.equals(method.getName()))
							{
								return method.invoke(e1).equals(value);
							}
						}
					} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					return false;
				};

				predicateList.add(first);

			} catch (InstantiationException | IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});

		return predicateList;
	}*/
	private List<Predicate<Mobile>> constructMobilePredicate(Map<String,String> allParams) {
		List<Predicate<Mobile>> predicateList = new ArrayList<Predicate<Mobile>>();
		allParams.forEach((methodName, value) -> {
			Predicate<Mobile>  predicate= null;
			switch (methodName){
			case "id":
				predicate= e -> e.getId().equals(Integer.parseInt(value));
				break;
			case "brand":
				predicate= e -> e.getBrand().equals(value);
				break;
			case "phone":
				predicate= e -> e.getPhone().equals(value);
				break;
			case "picture":
				predicate= e -> e.getPicture().equals(value);
				break;
			case "announceDate":
				predicate= e -> e.getRelease().getAnnounceDate().equals(value);
				break;
			case "priceEur":
				predicate= e -> e.getRelease().getPriceEur().equals(Double.parseDouble(value));
				break;
			case "sim":
				predicate= e -> e.getSim().equals(value);
				break;
			case "resolution":
				predicate= e -> e.getResolution().equals(value);
				break;
			case "audioJack":
				predicate= e -> e.getHardware().getAudioJack().equals(value);
				break;
			case "gps":
				predicate= e -> e.getHardware().getGps().equals(value);
				break;
			case "battery":
				predicate= e -> e.getHardware().getBattery().equals(value);
				break;

			}
			predicateList.add(predicate);
		});
		return predicateList;
	}
}
