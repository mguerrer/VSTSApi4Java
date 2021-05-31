package cl.set;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalTime;

/**
 * Unit test for simple App.
 */
class VSTSApi4JavaTest {
    static String OrganizationUrl = "https://dev.azure.com/marcosguerrerow/";
    static String TeamProjectName = "GasConnect";
    static String PAT = "zzdfjwfbpwofxhcbxhsvyqydtrq7hi754c7vvjm5cpyreue6277q";
    /*** 
     *            Id    Conf        Test 
     * TestPoints 1458  21 iOS      734 - "UAT-Registro de nuevo usuario"
     *            1459  22 Android  734 - "UAT-Registro de nuevo usuario"
     *            1460  21 iOS      739 - "UAT-usuario se conecta a su cuenta"
     *            1461  22 Android  739 - "UAT-usuario se conecta a su cuenta"
     */ 
    @Test
    void GetWorkItem() {
        Integer WorkItemId = 1208;
        VSTSApi4Java VSTS = new VSTSApi4Java(OrganizationUrl, TeamProjectName, PAT);
        VSTS.WorkItems.GetWorkItem(WorkItemId);
    }
    @Test
    void CreateTestRun() {
        Integer TestPlanId = 1859;
        Integer TestCaseId = 1459;
        VSTSApi4Java VSTS = new VSTSApi4Java(OrganizationUrl, TeamProjectName, PAT);

        String[] TestPoints = { "1458", "1459", "1460", "1461"};
        Integer runId = VSTS.Runs.CreateTestRun( OrganizationUrl, TeamProjectName, "Mi Run-"+LocalTime.now(), TestPlanId, TestPoints);
        //String testResultId = UpdateTestPoints(  OrganizationUrl, TeamProjectName, runId, TestPlanId, TestPoints,  "{\"outcome\": \"Failed\"}" );
        //String testResultId = AddTestResultToRun(  OrganizationUrl, TeamProjectName, runId, TestPlanId, TestCaseId, 22,  "Passed" );
        //String testResultId = AddTestResultToTestPlanPoint(  OrganizationUrl, TeamProjectName, TestPlanId, Configuration,  Outcome );

    }
}
