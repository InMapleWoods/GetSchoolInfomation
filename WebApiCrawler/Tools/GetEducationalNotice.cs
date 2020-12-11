using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Text;
using System.Text.RegularExpressions;

namespace WebApiCrawler.Tools
{
    /// <summary>
    /// 获取教务通知
    /// </summary>
    public class GetEducationalNotice
    {
        /// <summary>
        /// 获取教务通知列表
        /// </summary>
        /// <returns>教务通知列表</returns>
        private List<string> GetMessageOnline()
        {
            string url = "http://jw.ncepu.edu.cn/jiaowuchu";
            HttpWebRequest req = (HttpWebRequest)WebRequest.Create(url);
            ServicePointManager.ServerCertificateValidationCallback = (sender, certificate, chain, sslPolicyErrors) => true;
            req.Method = "GET";// POST OR GET， 如果是GET, 则没有第二步传参，直接第三步，获取服务端返回的数据
            req.AllowAutoRedirect = false;//服务端重定向。一般设置false
            req.AuthenticationLevel = System.Net.Security.AuthenticationLevel.None;
            req.ServerCertificateValidationCallback = (sender, certificate, chain, sslPolicyErrors) => { return true; };
            string ResponseGet = "";
            using (HttpWebResponse resp = (HttpWebResponse)req.GetResponse())
            {
                using (StreamReader reader = new StreamReader(resp.GetResponseStream()))
                {
                    ResponseGet = reader.ReadToEnd();
                }

            }
            return DealWithResponse(ResponseGet);
        }

        private static List<string> DealWithResponse(string ResponseGet)
        {
            string str = ResponseGet.Replace("\r\n", "");
            str = str.Replace("list-group-item highlighted", "list-group-item ");
            string ResponseGetPattern = "<div class=\"panel-body infopanel\">        <ul class=\"list-group\">                (.*)        </ul>    </div></div><div class=\"panel panel-default panel-infolist\">    <div class=\"panel-heading\">        <span class=\"glyphicon glyphicon-th-list\"></span>        <span class=\"panel-heading-title\">教务新闻</span>";
            Match resultGet = Regex.Match(str, ResponseGetPattern);
            string Data = resultGet.Groups[1].Value.ToString();
            ResponseGetPattern = "<span>(.{0,50})</span>";
            MatchCollection resultGet2 = Regex.Matches(Data, ResponseGetPattern);
            Data = resultGet2[1].Groups[1].Value.ToString();
            List<string> temp = new List<string>();
            foreach (Match data in resultGet2)
            {
                temp.Add(data.Groups[1].Value.ToString());
            }
            return temp;
        }

        /// <summary>
        /// 获取教务通知字符串
        /// </summary>
        /// <returns>教务通知</returns>
        public string GetNoticeStr()
        {
            List<string> notices = GetMessageOnline();
            StringBuilder builder = new StringBuilder();
            foreach (var s in notices)
            {
                builder.Append(s);
                builder.Append("\r\n");
            }
            return builder.ToString();
        }
    }
}
