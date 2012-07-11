package com.cloudspokes.dynamodb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapperConfig.SaveBehavior;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodb.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodb.model.AttributeValue;
import com.amazonaws.services.dynamodb.model.ComparisonOperator;
import com.amazonaws.services.dynamodb.model.Condition;
import com.cloudspokes.dynamodb.domain.Loan;
import com.cloudspokes.dynamodb.helper.Helper;

@Controller
public class HomeController {

	static DynamoDBMapper dynamoDB;
	private String tableName = "kiva-loans";

	public HomeController() {
		try {
			setup();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Displays the list of loans from the table
	 */
	@RequestMapping(value = "/loans", method = RequestMethod.GET)
	public String loans(Locale locale,
			@RequestParam(value = "keyword", required = false) String keyword,
			Model model) {

		List<Loan> loans = new ArrayList<Loan>();
		DynamoDBScanExpression exp = new DynamoDBScanExpression();
		if (keyword != null) {
			Condition condition = new Condition().withComparisonOperator(
					ComparisonOperator.EQ.toString()).withAttributeValueList(
					new AttributeValue().withS(keyword));
			exp.addFilterCondition("country", condition);
		}
		PaginatedScanList<Loan> result = dynamoDB.scan(Loan.class, exp);
		for (Loan l : result) {
			loans.add(l);
		}
		model.addAttribute("loans", loans);
		return "loans";
	}

	/**
	 * Displays a loan item
	 */
	@RequestMapping(value = "/show/{id}", method = RequestMethod.GET)
	public String show(@PathVariable String id, Locale locale, Model model) {
		model.addAttribute("loan", getLoan(id));
		return "show";
	}

	/**
	 * Displays a form to create a new loan item
	 */
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String newLoan(Locale locale, Model model) {
		model.addAttribute("loan", new Loan());
		return "new";
	}

	/**
	 * Inserts a new loan item into dynamodb
	 */
	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public String addLoan(@ModelAttribute("loan") Loan loan,
			BindingResult result) {
		try {
			dynamoDB.save(loan);
		} catch (AmazonServiceException ase) {
			System.err.println("Failed to create item in " + tableName);
		}
		return "redirect:show/" + loan.getId();
	}

	/**
	 * Displays the item for editing
	 */
	@RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
	public String editLoan(@PathVariable String id, Locale locale, Model model) {
		model.addAttribute("loan", getLoan(id));
		return "edit";
	}

	/**
	 * Submits the updates loan data to dynamodb
	 */
	@RequestMapping(value = "/edit/{id}", method = RequestMethod.POST)
	public String updateLoan(@PathVariable String id,
			@ModelAttribute("loan") Loan loan, BindingResult result) {

		// update the item to the table
		try {
			dynamoDB.save(loan, new DynamoDBMapperConfig(SaveBehavior.UPDATE));
		} catch (AmazonServiceException ase) {
			System.err.println("Failed to update item: " + ase.getMessage());
		}

		return "redirect:../show/" + id;
	}

	/**
	 * Fetches loan data from Kiva an inserts it into dynamodb
	 */
	@RequestMapping(value = "/loadData", method = RequestMethod.GET)
	public String loadData(Locale locale, Model model) {

		// delete all of the current loans
		Helper.deleteAllLoans(dynamoDB);

		// make the REST call to kiva
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet getRequest = new HttpGet(
				"http://api.kivaws.org/v1/loans/newest.json");
		getRequest.addHeader("accept", "application/json");
		HttpResponse response;
		String payload = "";

		try {
			response = httpClient.execute(getRequest);

			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(response.getEntity().getContent())));
			payload = br.readLine();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JSONObject json = (JSONObject) JSONSerializer.toJSON(payload);
		// get the array of loans
		JSONArray loans = json.getJSONArray("loans");

		List<Loan> list = new ArrayList<Loan>();
		for (int i = 0; i < loans.size(); ++i) {
			JSONObject loan = loans.getJSONObject(i);
			Loan o = new Loan();
			o.setId(Integer.valueOf(loan.getString("id")));
			o.setName(loan.getString("name"));
			o.setStatus(loan.getString("status"));
			o.setFunded_amount(Double.valueOf(loan.getString("funded_amount")));
			o.setActivity(loan.getString("activity"));
			o.setUse(loan.getString("use"));
			o.setCountry(loan.getJSONObject("location").getString("country"));
		}
		try {
			dynamoDB.batchSave(list);
		} catch (AmazonServiceException ase) {
			System.err.println("Failed to create item in " + tableName);
		}

		httpClient.getConnectionManager().shutdown();

		return "redirect:loans";
	}

	/**
	 * Displays the home page
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		return "home";
	}

	/**
	 * Fetches a specific loan item
	 */
	Loan getLoan(String id) {
		Loan loan = dynamoDB.load(Loan.class, id);
		return loan;
	}

	synchronized void setup() throws Exception {
		dynamoDB = Helper.mapper();
	}

}
