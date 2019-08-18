package web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Client {

	private static final String username = "";// TODO fill your username
	private static final String password = "";// TODO fill your password

	private static Client def;

	private static final String ADD_L = "pageContent_CourseList_";
	private static final String ADD_S = "Sections_";
	private static final String ADD_A = "_AddLink";
	private static final String ADD_0 = "Primary";
	private static final String ADD_1 = "Secondary";

	public static boolean fight(String quar, String subj, int... is) {
		Client c = getClient();
		boolean suc = c.goAddPage(quar, subj, is);
		if (!suc)
			return false;
		return c.doAdd();

	}

	public static Client getClient() {
		if (def == null)
			def = new Client();
		return def;
	}

	private static String getBtn(int... is) {
		String ans = ADD_L + ADD_0 + ADD_S + is[0];
		if (is.length == 2)
			return ans + ADD_A + ADD_0 + "_" + is[1];
		else
			return ans + "_" + ADD_1 + ADD_S + is[1] + ADD_A + ADD_1 + "_" + is[2];
	}

	private HttpClient client;

	public Client() {
		client = HttpClients.createDefault();
		if (!login())
			System.out.println("failed to login");

	}

	protected String getData(String quarter, String subject) {
		if (!getInfo(quarter, subject))
			return "";
		try {
			HttpGet get = new HttpGet("https://my.sa.ucsb.edu/gold/ResultsFindCourses.aspx");
			HttpResponse response = client.execute(get);
			return EntityUtils.toString(response.getEntity());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	private boolean doAdd() {
		try {
			HttpGet get = new HttpGet("https://my.sa.ucsb.edu/gold/AddStudentSchedule.aspx");
			HttpResponse res = client.execute(get);
			String html = EntityUtils.toString(res.getEntity());
			Document doc = Jsoup.parse(html);
			String form = doc.getElementsByTag("form").get(0).attr("action");
			if (!form.equals("./AddStudentSchedule.aspx")) {
				System.out.println("failed to login");
				return false;
			}
			String state = doc.getElementById("__VIEWSTATE").val();
			String gener = doc.getElementById("__VIEWSTATEGENERATOR").val();

			HttpPost post = new HttpPost("https://my.sa.ucsb.edu/gold/AddStudentSchedule.aspx");

			// Request parameters and other properties.
			List<NameValuePair> params = new ArrayList<NameValuePair>(5);
			params.add(new BasicNameValuePair("__VIEWSTATE", state));
			params.add(new BasicNameValuePair("__VIEWSTATEGENERATOR", gener));
			params.add(new BasicNameValuePair("ctl00$pageContent$AddToScheduleButton", "Add to Schedule"));

			post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

			// Execute and get the response.
			HttpResponse response = client.execute(post);
			String str = EntityUtils.toString(response.getEntity());
			Document red = Jsoup.parse(str);
			String link = red.getElementsByTag("a").get(0).attr("href");
			try {
				link = link.split("/")[2];
				if (!link.equals("StudentSchedule.aspx")) {
					System.out.println("failed, redirect to: " + link);
					return false;
				}
				return true;
			} catch (Exception e) {
				System.out.println(str);
				return false;
			}

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

	}

	private boolean getInfo(String quarter, String subject) {

		try {
			HttpGet get = new HttpGet("https://my.sa.ucsb.edu/gold/BasicFindCourses.aspx");
			HttpResponse res = client.execute(get);
			String html = EntityUtils.toString(res.getEntity());
			Document doc = Jsoup.parse(html);
			String form = doc.getElementsByTag("form").get(0).attr("action");
			if (!form.equals("./BasicFindCourses.aspx")) {
				System.out.println("failed to login");
				return false;
			}
			String state = doc.getElementById("__VIEWSTATE").val();
			String gener = doc.getElementById("__VIEWSTATEGENERATOR").val();

			HttpPost post = new HttpPost("https://my.sa.ucsb.edu/gold/BasicFindCourses.aspx");

			// Request parameters and other properties.
			List<NameValuePair> params = new ArrayList<NameValuePair>(5);
			params.add(new BasicNameValuePair("__VIEWSTATE", state));
			params.add(new BasicNameValuePair("__VIEWSTATEGENERATOR", gener));
			params.add(new BasicNameValuePair("ctl00$pageContent$quarterDropDown", quarter));
			params.add(new BasicNameValuePair("ctl00$pageContent$subjectAreaDropDown", subject));
			params.add(new BasicNameValuePair("ctl00$pageContent$searchButton", "Search"));

			post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

			// Execute and get the response.
			HttpResponse response = client.execute(post);
			String str = EntityUtils.toString(response.getEntity());
			Document red = Jsoup.parse(str);
			String link = red.getElementsByTag("a").get(0).attr("href");
			try {
				link = link.split("/")[2];
				if (!link.equals("ResultsFindCourses.aspx")) {
					System.out.println("failed, redirect to: " + link);
					return false;
				}
				return true;
			} catch (Exception e) {
				System.out.println(str);
				return false;
			}

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean goAddPage(String quarter, String subject, int... is) {
		try {
			Document doc = Jsoup.parse(getData(quarter, subject));
			String form = doc.getElementsByTag("form").get(0).attr("action");
			if (!form.equals("./ResultsFindCourses.aspx")) {
				System.out.println("failed to login: " + form);
				return false;
			}
			String state = doc.getElementById("__VIEWSTATE").val();
			String gener = doc.getElementById("__VIEWSTATEGENERATOR").val();
			String addbtn = doc.getElementById(getBtn(is)).attr("name");
			HttpPost post = new HttpPost("https://my.sa.ucsb.edu/gold/ResultsFindCourses.aspx");

			// Request parameters and other properties.
			List<NameValuePair> params = new ArrayList<NameValuePair>(5);
			params.add(new BasicNameValuePair("__VIEWSTATE", state));
			params.add(new BasicNameValuePair("__VIEWSTATEGENERATOR", gener));
			params.add(new BasicNameValuePair(addbtn, "Add"));

			post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

			// Execute and get the response.
			HttpResponse response = client.execute(post);
			String str = EntityUtils.toString(response.getEntity());
			Document red = Jsoup.parse(str);
			String link = red.getElementsByTag("a").get(0).attr("href");
			link = link.split("/")[2];
			if (!link.equals("AddStudentSchedule.aspx")) {
				System.out.println("failed, redirect to: " + link);
				return false;
			}
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

	}

	private boolean login() {
		try {
			HttpGet get = new HttpGet("https://my.sa.ucsb.edu/gold/Login.aspx");
			HttpResponse res = client.execute(get);
			Document doc = Jsoup.parse(EntityUtils.toString(res.getEntity()));
			String state = doc.getElementById("__VIEWSTATE").val();
			String gener = doc.getElementById("__VIEWSTATEGENERATOR").val();
			String event = doc.getElementById("__EVENTVALIDATION").val();

			HttpPost post = new HttpPost("https://my.sa.ucsb.edu/gold/Login.aspx");

			// Request parameters and other properties.
			List<NameValuePair> params = new ArrayList<NameValuePair>(6);
			params.add(new BasicNameValuePair("__VIEWSTATE", state));
			params.add(new BasicNameValuePair("__VIEWSTATEGENERATOR", gener));
			params.add(new BasicNameValuePair("__EVENTVALIDATION", event));
			params.add(new BasicNameValuePair("ctl00$pageContent$userNameText", username));
			params.add(new BasicNameValuePair("ctl00$pageContent$passwordText", password));
			params.add(new BasicNameValuePair("ctl00$pageContent$loginButton", "Login"));

			post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

			// Execute and get the response.
			HttpResponse response = client.execute(post);
			int code = response.getStatusLine().getStatusCode();
			if (code == 302)
				return true;
			System.out.println(code);
			HttpEntity entity = response.getEntity();
			String cont = EntityUtils.toString(entity);
			System.out.println(cont);
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

}
