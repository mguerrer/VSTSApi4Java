package cl.set;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.HttpPost;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class VSTSApi4Java {

    String ServiceUrl = "https://dev.azure.com/marcosguerrerow/";
    String TeamProjectName = "GasConnect";
    Integer WorkItemId = 1208;
    String PAT = "zzdfjwfbpwofxhcbxhsvyqydtrq7hi754c7vvjm5cpyreue6277q";
    HttpURLConnection con = null;
    public WorkItems WorkItems = new WorkItems();
    public Runs Runs = new Runs();

    VSTSApi4Java(String OrganizationURL, String TeamProjectName, String PAT) {
        ServiceUrl = OrganizationURL;
        this.TeamProjectName = TeamProjectName;
        this.PAT = PAT;
    }
    public class RequestResponse {
        int responseCode;
        String responseBody="";
    }
    public class WorkItem {
        String id;
        Map<String, String> fields;
    }
    public class WorkItems {
        /***
         * Usa API para retornar un workitem por ID.
         *      GET https://dev.azure.com/{organization}/{project}/_apis/wit/workitems/{id}?api-version=6.0
         * https://docs.microsoft.com/en-us/rest/api/azure/devops/wit/work%20items/get%20work%20item?view=azure-devops-rest-6.0
         * @param WorkItemId
         * @return
         */
        public WorkItem GetWorkItem( Integer WorkItemId) {
            String UrlEndGetWorkItemById = "/_apis/wit/workitems/";
            WorkItem WorkItem = new WorkItem();
            try {

                String AuthStr = ":" + PAT;
                Base64 base64 = new Base64();
                String encodedPAT = new String(base64.encode(AuthStr.getBytes()));

                URL url = new URL(ServiceUrl + TeamProjectName + UrlEndGetWorkItemById + WorkItemId.toString());
                con = (HttpURLConnection) url.openConnection();
                con.setRequestProperty("Authorization", "Basic " + encodedPAT);
                con.setRequestMethod("GET");

                int status = con.getResponseCode();

                if (status == 200){
                    System.out.println("Conectado!");
                    String responseBody;
                    try (Scanner scanner = new Scanner(con.getInputStream())) {
                        responseBody = scanner.useDelimiter("\\A").next();
                        System.out.println(responseBody);
                    }

                    try {
                        Object obj = new JSONParser().parse(responseBody);
                        JSONObject jo = (JSONObject) obj;

                        String WIID = (String) jo.get("id").toString();
                        Map<String, String> fields = (Map<String, String>) jo.get("fields");
                        System.out.println("WorkItemId - " + WIID);
                        System.out.println("WorkItemTitle - " + fields.get("System.Title"));
                        WorkItem.id = WIID;
                        WorkItem.fields = fields;
                        return WorkItem;
                    } catch (ParseException e) {
                        e.printStackTrace();
                        WorkItem = null;
                    }
                }           

                con.disconnect();

            } catch (IOException e) {
                e.printStackTrace();
                WorkItem = null;
            }
            return WorkItem;
        }

    }

    public class Runs{
        Integer CreateTestRun( String OrganizationUrl, String TeamProjectName, String RunName, Integer TestPlanId, String[] TestPoints){
            RequestResponse RequestResponse = new RequestResponse();

            try { // LLama a VSTS.
                URL url = new URL(ServiceUrl + TeamProjectName + "/_apis/test/runs/?api-version=6.1-preview.3" );
                String RequestBody = "{\"name\": \""+RunName+"\",\"plan\": { \"id\": \""+ TestPlanId +"\"},\"pointIds\": ["+TestPoints[0]+"]}";
    
                RequestResponse = SendPOSTRequest(url, RequestBody);
            } catch (IOException e) {
                System.out.printf("Error en POST:%s", e.getMessage());
                System.exit(1);
            }
            int status = RequestResponse.responseCode;

            if (status == 200){
                System.out.println("Run "+ RunName + " creada correctamente.");
                try {
                    Object obj = new JSONParser().parse(RequestResponse.responseBody);
                    JSONObject jo = (JSONObject) obj;

                    String runId = (String) jo.get("id").toString();
                    String name  = (String) jo.get("name").toString();
                    String uri   = (String) jo.get("url").toString();
                    //Map<String, String> fields = (Map<String, String>) jo.get("fields");
                    System.out.println("runID - " + runId);
                    System.out.println("Name - " + name);
                    System.out.println("URL - " + uri);
                    return Integer.parseInt(runId);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return -1;
                }
            }  
            else return -2;         

        }
        /*
        public class TestResults {
            String AddTestResultToRun(  String OrganizationUrl, String TeamProjectName, Integer runId, Integer TestPlanId, Integer TestCaseId, String Configuration, String Outcome ) {
                RequestResponse RequestResponse = new RequestResponse();

                try { // LLama a VSTS.
                    URL url = new URL(ServiceUrl + TeamProjectName + "/_apis/test/runs/?api-version=6.1-preview.3" );
                    String RequestBody = "{\"name\": \""+RunName+"\",\"plan\": { \"id\": \""+ TestPlanId +"\"},\"pointIds\": ["+TestPoints[0]+"]}";
        
                    RequestResponse = SendPOSTRequest(url, RequestBody);
                } catch (IOException e) {
                    System.out.printf("Error en POST:%s", e.getMessage());
                    System.exit(1);
                }
                int status = RequestResponse.responseCode;                
            }

        }*/

        private RequestResponse SendPOSTRequest(URL url, String RequestBody )
                throws IOException, ProtocolException, UnsupportedEncodingException {
            
            String AuthStr = ":" + PAT;
            Base64 base64 = new Base64();
            String encodedPAT = new String(base64.encode(AuthStr.getBytes()));

            con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Authorization", "Basic " + encodedPAT);
            System.out.println("PAT="+encodedPAT);

            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestMethod("POST");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            System.out.println("RequestBody="+RequestBody);
            //Create the Request Body
            try(OutputStream os = con.getOutputStream()) {
                byte[] input = RequestBody.getBytes("utf-8");
                os.write(input, 0, input.length);			
            }
            catch(Exception e) {
                e.printStackTrace();
                return null;
            }
            RequestResponse RequestResponse = new RequestResponse();
            RequestResponse.responseCode = con.getResponseCode();
            try(BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println(response.toString());
                    RequestResponse.responseBody = response.toString();
            }
            catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            con.disconnect();
            return RequestResponse;
        }
    }

}