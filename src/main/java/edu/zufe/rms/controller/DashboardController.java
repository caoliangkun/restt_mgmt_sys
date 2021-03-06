package edu.zufe.rms.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;

import edu.zufe.rms.model.Food;
import edu.zufe.rms.model.Order;
import edu.zufe.rms.model.OrderStats;
import edu.zufe.rms.model.Payment;
import edu.zufe.rms.model.PaymentStats;
import edu.zufe.rms.service.FoodService;
import edu.zufe.rms.service.OrderService;
import edu.zufe.rms.service.PaymentService;

@Controller
public class DashboardController {
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private PaymentService paymentService;
	
	@Autowired
	private FoodService foodService;

	@GetMapping(path = "/dashboard")
	public ModelAndView dashboard(@RequestParam(name = "year", required = false, defaultValue = "2018") String year) {
		ModelAndView modelAndView = new ModelAndView("admin/dashboard");
		modelAndView.addObject("year", year);

		OrderStats orderStats = getOrderStats(year);
		String orderStatsJson = JSON.toJSONString(orderStats);
		modelAndView.addObject("orderStats", orderStatsJson);
		
		PaymentStats paymentStats = getPaymentStats(year);
		String paymentStatsJson = JSON.toJSONString(paymentStats);
		modelAndView.addObject("incomeStats", paymentStatsJson);
		
		String numOfTodaysOrders = String.valueOf(getNumOfTodaysOrders());
		String todaysIncome = String.valueOf(getTodaysIncome());
		modelAndView.addObject("numOfTodaysOrders", numOfTodaysOrders);
		modelAndView.addObject("todaysIncome", todaysIncome);
		
		List<Food> foods = foodService.findAll();
		List<Food> rankedFoods = new ArrayList<>();
		int v = 5;
		for (Food f : foods) {
			rankedFoods.add(f);
			v--;
			if (v == 0) 
				break;
		}
		modelAndView.addObject("one", rankedFoods.get(0).getName());
		modelAndView.addObject("two", rankedFoods.get(1).getName());
		modelAndView.addObject("three", rankedFoods.get(2).getName());
		modelAndView.addObject("four", rankedFoods.get(3).getName());
		modelAndView.addObject("five", rankedFoods.get(4).getName());
		return modelAndView;
	}
	
	@GetMapping(path = "/query") 
	public ModelAndView query(@RequestParam(name = "date") String date) throws ParseException {
		System.out.println(date);
		ModelAndView modelAndView = new ModelAndView("admin/query");
		if (date == "") {
			return modelAndView;
		}
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date d = df.parse(date);
		String numOfOrders = String.valueOf(getSpecificDateOrders(d));
		modelAndView.addObject("numOfOrders", numOfOrders);
		String income = String.valueOf(getSpecificDateIncome(d));
		modelAndView.addObject("income", income);
		modelAndView.addObject("date", date);
		return modelAndView;
	}

	@GetMapping(path = "/queryOrderById")
	public String queryOrderById(@RequestParam(name = "id") String id, Model model) {
		Order order = orderService.findById(id);
		model.addAttribute("orderId", order.getId().toString());
		model.addAttribute("totalPrice", order.getTotalPrice().toString());
		model.addAttribute("customerId", order.getCustomer().getId().toString());
		model.addAttribute("time", order.getUpdateAt());
		return "admin/query";
	}
	
	@ResponseBody
	@GetMapping(path = "/queryPaymentById")
	public String queryPaymentById(@RequestParam(name = "id") String id) {
		Payment payment = paymentService.findById(Long.valueOf(id));
		String paymentStr = JSON.toJSONString(payment);
		System.out.println(paymentStr);
		return paymentStr;
	}
	@ResponseBody
	@GetMapping(path = "/ajax")
	public String ajax(@RequestParam(name = "id") String id) {
		Payment payment = paymentService.findById(Long.valueOf(id));
		String paymentStr = JSON.toJSONString(payment);
		return paymentStr;
	}
	
	@GetMapping(path = "/foodRank")
	public String foodRank(@RequestParam(name = "volume") String volume, Model model) {
		List<Food> foods = foodService.findAll();
		List<Food> rankedFoods = new ArrayList<>();
		int v = Integer.valueOf(volume);
		for (Food f : foods) {
			rankedFoods.add(f);
			v--;
			if (v == 0) 
				break;
		}
		model.addAttribute("rankedFoods", rankedFoods);
		return "admin/query";
	}

	private int getSpecificDateOrders(Date d) {
		List<Order> orders = orderService.findBySpecificDate(d);
		
		int num = orders.size();
		return num;
	}

	private int getNumOfTodaysOrders() {
		List<Order> orders = orderService.findByDate();
		int num = orders.size();
		return num;
	}


	private double getSpecificDateIncome(Date d) {
		List<Payment> payments = paymentService.findBySpecificDate(d);
		double income = 0.0;
		for (Payment pay : payments) {
			income += pay.getAmount();
		}
		return income;
	}
	private double getTodaysIncome() {
		List<Payment> payments = paymentService.findByDate();
		double income = 0.0;
		for (Payment pay : payments) {
			income += pay.getAmount();
		}
		return income;
	}



	private OrderStats getOrderStats(String year) {
		OrderStats orderStats = new OrderStats();
		Calendar calendar = Calendar.getInstance();
		List<Order> orders = orderService.findAll(year);
		for (Order order : orders) {	
			if (order.getCreatedAt() != null) {			
				calendar.setTime(order.getCreatedAt());
				if (calendar.get(Calendar.MONTH) == 0) {
					orderStats.setJan(orderStats.getJan() + 1);
				} else if (calendar.get(Calendar.MONTH) == 1) {
					orderStats.setFeb(orderStats.getFeb() + 1);
				} else if (calendar.get(Calendar.MONTH) == 2) {
					orderStats.setMar(orderStats.getMar() + 1);
				} else if (calendar.get(Calendar.MONTH) == 3) {
					orderStats.setApr(orderStats.getApr() + 1);
				} else if (calendar.get(Calendar.MONTH) == 4) {
					orderStats.setMay(orderStats.getMay() + 1);
				} else if (calendar.get(Calendar.MONTH) == 5) {
					orderStats.setJun(orderStats.getJun() + 1);
				} else if (calendar.get(Calendar.MONTH) == 6) {
					orderStats.setJul(orderStats.getJul() + 1);
				} else if (calendar.get(Calendar.MONTH) == 7) {
					orderStats.setAug(orderStats.getAug() + 1);
				} else if (calendar.get(Calendar.MONTH) == 8) {
					orderStats.setSep(orderStats.getSep() + 1);
				} else if (calendar.get(Calendar.MONTH) == 9) {
					orderStats.setOct(orderStats.getOct() + 1);
				} else if (calendar.get(Calendar.MONTH) == 10) {
					orderStats.setNov(orderStats.getNov() + 1);
				} else if (calendar.get(Calendar.MONTH) == 11) {
					orderStats.setDec(orderStats.getDec() + 1);
				}
			}
		}
		return orderStats;
	}



	private PaymentStats getPaymentStats(String year) {
		PaymentStats paymentStats = new PaymentStats();
		Calendar calendar = Calendar.getInstance();
		List<Payment> payments = paymentService.findAll(year);
		for (Payment payment : payments) {
			if (payment.getPayAt() != null) {
				calendar.setTime(payment.getPayAt());
				if (calendar.get(Calendar.MONTH) == 0) {
					paymentStats.setJan(paymentStats.getJan() + payment.getAmount());
				} else if (calendar.get(Calendar.MONTH) == 1) {
					paymentStats.setFeb(paymentStats.getFeb() + payment.getAmount());
				} else if (calendar.get(Calendar.MONTH) == 2) {
					paymentStats.setMar(paymentStats.getMar() + payment.getAmount());
				} else if (calendar.get(Calendar.MONTH) == 3) {
					paymentStats.setApr(paymentStats.getApr() + payment.getAmount());
				} else if (calendar.get(Calendar.MONTH) == 4) {
					paymentStats.setMay(paymentStats.getMay() + payment.getAmount());
				} else if (calendar.get(Calendar.MONTH) == 5) {
					paymentStats.setJun(paymentStats.getJun() + payment.getAmount());
				} else if (calendar.get(Calendar.MONTH) == 6) {
					paymentStats.setJul(paymentStats.getJul() + payment.getAmount());
				} else if (calendar.get(Calendar.MONTH) == 7) {
					paymentStats.setAug(paymentStats.getAug() + payment.getAmount());
				} else if (calendar.get(Calendar.MONTH) == 8) {
					paymentStats.setSep(paymentStats.getSep() + payment.getAmount());
				} else if (calendar.get(Calendar.MONTH) == 9) {
					paymentStats.setOct(paymentStats.getOct() + payment.getAmount());
				} else if (calendar.get(Calendar.MONTH) == 10) {
					paymentStats.setNov(paymentStats.getNov() + payment.getAmount());
				} else if (calendar.get(Calendar.MONTH) == 11) {
					paymentStats.setDec(paymentStats.getDec() + payment.getAmount());
				}
			}
		}
		return paymentStats;
	}


}
