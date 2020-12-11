using Microsoft.AspNetCore.Mvc;
using System;

namespace WebApiCrawler.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class ExamController : ControllerBase
    {
        Tools.GetStudentGradeOrExam gradeOrExam = new Tools.GetStudentGradeOrExam();
        // GET: api/<controller>
        [HttpGet]
        public IActionResult Get()
        {
            return Ok(gradeOrExam.GetStudentExam(""));
        }

        [HttpGet("{date}")]
        public IActionResult GetByDate(string date)
        {
            return Ok(gradeOrExam.GetStudentExam(date));
        }

        [HttpPost]
        public IActionResult Get([FromBody] dynamic accountPassword, string date)
        {
            try
            {
                dynamic accountPasswordObject = Newtonsoft.Json.JsonConvert.DeserializeObject<dynamic>(accountPassword.ToString());
                string account = accountPasswordObject.account;
                string password = accountPasswordObject.password;
                return Ok(gradeOrExam.GetStudentExam(account, password, date));
            }
            catch (Exception ex)
            {
                return NotFound(ex.Message);
            }
        }
    }
}