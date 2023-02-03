package com.middleware.bean;

import org.apache.camel.Exchange;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;

/**
 * @author Redhat
 */

public class ProcessorBean {

	private static final Logger logger = LoggerFactory.getLogger(ProcessorBean.class);
	private static final String CONTEXT = "context";

	public String enlistaAlertas(Exchange ex) {

		String bodyIn = ex.getIn().getBody(String.class);
		String bodyMessage = "";
		JSONObject arr = new JSONObject((bodyIn));

		try {
			for (int i = 0; i < arr.optJSONArray("alerts").length(); i++) {
				JSONObject alertData = new JSONObject(arr.optJSONArray("alerts").get(i).toString());
				
				// obtener informacion de annotations y labels
				String namespace;
				String alertname;
				String description;
				String severity;
				String message;
				
				// valida detalles de la alerta				
				if (alertData.getJSONObject("labels").has("namespace")) {
					namespace = alertData.getJSONObject("labels").getString("namespace");
				} else {
					namespace = "namespace not specified";
				}

				if (alertData.getJSONObject("labels").has("alertname")) {
					alertname = alertData.getJSONObject("labels").getString("alertname");
				} else {
					alertname = "alertname not specified";
				}				
				
				if (alertData.getJSONObject("labels").has("description")) {
					description = alertData.getJSONObject("labels").getString("description");
				} else if (alertData.getJSONObject("annotations").has("description")) {
					description = alertData.getJSONObject("annotations").getString("description");
				} else if (alertData.getJSONObject("annotations").has("message")) {
                    description = alertData.getJSONObject("annotations").getString("message");
				} else{
					description = "description not specified";
				}				
				
				if (alertData.getJSONObject("labels").has("severity")) {
					severity = alertData.getJSONObject("labels").getString("severity");
				} else {
					severity = "severity not specified";
				}				
				
				// construccion de body hacia teams
				bodyMessage += "<br/><br/>"
						+ "\\n- <h3>alertname: <i>" + alertname +"</i></h3>"
						+ "\\n- <h3>namespace:</h3> " + namespace
						+ "\\n- <h3>description:</h3> " + description.replace("\"", "'")
						+ "\\n- <h3>severity:</h3> " + severity
				;
			}
		} catch (DataAccessException error) {
			logger.error(CONTEXT, error);
		}

		return bodyMessage;

	}

}
