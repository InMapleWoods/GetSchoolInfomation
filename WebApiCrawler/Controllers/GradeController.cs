using Microsoft.AspNetCore.Mvc;
using System;

// For more information on enabling Web API for empty projects, visit https://go.microsoft.com/fwlink/?LinkID=397860

namespace WebApiCrawler.Controllers
{
    [Route("api/[controller]")]
    public class GradeController : ControllerBase
    {
        Tools.GetStudentGradeOrExam gradeOrExam = new Tools.GetStudentGradeOrExam();
        [HttpGet]
        public IActionResult Get()
        {
            return Ok(gradeOrExam.GetStudentGrade(false));
        }

        [HttpGet("{date}")]
        public IActionResult GetByDate(string date, bool isRank)
        {
            return Ok(gradeOrExam.GetStudentGrade(isRank, date));
        }

        [HttpPost]
        public IActionResult Post([FromBody] dynamic accountPassword, string date, bool isRank)
        {
            try
            {
                dynamic accountPasswordObject = Newtonsoft.Json.JsonConvert.DeserializeObject<dynamic>(accountPassword.ToString());
                string account = accountPasswordObject.account;
                string password = accountPasswordObject.password;
                return Ok(gradeOrExam.GetStudentGrade(isRank, account, password, date));
            }
            catch (Exception ex)
            {
                return NotFound(ex.Message);
            }
        }

    }
}
