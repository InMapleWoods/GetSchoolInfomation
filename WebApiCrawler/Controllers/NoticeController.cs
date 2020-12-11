using Microsoft.AspNetCore.Mvc;

namespace WebApiCrawler.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class NoticeController : ControllerBase
    {
        Tools.GetEducationalNotice getNotice = new Tools.GetEducationalNotice();
        // GET: api/<controller>
        [HttpGet]
        public string Get()
        {
            return getNotice.GetNoticeStr();
        }
    }
}