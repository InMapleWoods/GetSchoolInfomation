using System;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Text;
using System.Text.RegularExpressions;

namespace WebApiCrawler.Tools
{
    /// <summary>
    /// 获取学生成绩
    /// </summary>
    public class GetStudentGradeOrExam
    {
        public string GetStudentGrade(bool isRank, string account, string password, string date = "")
        {
            CookieContainer cookieContainer = getInfo(account, password);
            //获取成绩
            string url = "https://jwxt.ncepu.edu.cn/jsxsd/kscj/cjcx_list";
            string postData = "kksj=" + GetTime(date) + "&kcxz=&kcmc=&xsfs=all";
            string ResponseGet = GetUrlInfo(url, cookieContainer, "POST", false, postData);
            string gradeList = ResponseGet.Replace("\r\n", "").Replace(" ", "").Replace("\t", "").Replace("&nbsp;", "");
            string score = handleGradeInfo(gradeList, isRank);
            return score;
        }
        public string GetStudentGrade(bool isRank, string date = "")
        {
            return GetStudentGrade(isRank, "201709001013", "llf99723", date);
        }
        private string GetGradeRank(string id, string grade)
        {
            CookieContainer cookieContainer = getInfo("201709001013", "llf99723");
            //获取成绩
            string url = "https://jwxt.ncepu.edu.cn/jsxsd/xspj/xspj_ckpm.do?jx0404id=" + id + "&tktime=" + ((DateTime.Now.ToUniversalTime().Ticks - 621355968000000000) / 10000).ToString();
            string ResponseGet = GetUrlInfo(url, cookieContainer, "GET", false);
            string rankList = ResponseGet.Replace("\r\n", "").Replace(" ", "").Replace("\t", "").Replace("&nbsp;", "");
            string rankPattern = "_table\"><tr><thclass=\"Nsb_r_list_thb\"scope=\"col\">成绩</th><thclass=\"Nsb_r_list_thb\"scope=\"col\">排名</th></tr>(.*)</table><divid";
            Match resultGrade = Regex.Match(rankList, rankPattern);
            string rankData = resultGrade.Groups[1].Value.ToString();
            MatchCollection resultClassGrade = Regex.Matches(rankData, @"<tr><td>((\d{1,3})|(\d{1,3}\.\d{1,2})|((优)|(良)|(中)|(及格)|(不及格)))</td><td>(\d{1,3})</td></tr>");
            for (int i = 0; i < resultClassGrade.Count; i++)
            {
                string gradeStr = resultClassGrade[i].Groups[1].Value.ToString();
                if (Regex.IsMatch(gradeStr, "^(优)|(良)|(中)|(及格)|(不及格)$"))
                {
                    if (gradeStr == grade)
                    {
                        int result;
                        if (int.TryParse(resultClassGrade[i].Groups[10].Value.ToString(), out result))
                        {
                            return result + "/" + resultClassGrade.Count;
                        }
                        else
                        {
                            return string.Empty;
                        }
                    }
                }
                else
                {
                    double rankGrade;
                    if (double.TryParse(gradeStr, out rankGrade))
                    {
                        if (rankGrade - double.Parse(grade) <= 1e-6)
                        {
                            int result;
                            if (int.TryParse(resultClassGrade[i].Groups[10].Value.ToString(), out result))
                            {
                                return result + "/" + resultClassGrade.Count;
                            }
                            else
                            {
                                return string.Empty;
                            }
                        }
                    }
                }
            }
            return string.Empty;
        }


        public string GetStudentExam(string account, string password, string date = "")
        {
            CookieContainer cookieContainer = getInfo(account, password);
            //获取考试
            string url = "https://jwxt.ncepu.edu.cn/jsxsd/xsks/xsksap_list";
            string postData = "xqlbmc=&xnxqid=" + GetTime(date) + "&kc=&ksjs=&jkls=";
            string ResponseGet = GetUrlInfo(url, cookieContainer, "POST", false, postData);
            string examList = ResponseGet.Replace("\r\n", "").Replace(" ", "").Replace("\t", "").Replace("&nbsp;", "");
            string exam = handleExamInfo(examList);
            return exam;
        }
        public string GetStudentExam(string date = "")
        {
            return GetStudentExam("201709001013", "llf99723", date);
        }

        private CookieContainer getInfo(string account, string password)
        {
            ServicePointManager.ServerCertificateValidationCallback = (sender, certificate, chain, sslPolicyErrors) => true;
            CookieContainer cookieContainer = new CookieContainer();
            Stream stream = new MemoryStream();
            string url = "https://jwxt.ncepu.edu.cn/Logon.do?method=logon&flag=sess";
            //获取加密KEY
            //获取响应内容
            string ResponseGet = GetUrlInfo(url, cookieContainer, "POST", false);
            string scode = ResponseGet.Split("#")[0];
            string sxh = ResponseGet.Split("#")[1];
            string code = account + "%%%" + password;
            string encoded = "";
            for (int i = 0; i < code.Length; i++)
            {
                if (i < 20)
                {
                    encoded = encoded + code.Substring(i, 1) + scode.Substring(0, int.Parse(sxh.Substring(i, 1)));
                    scode = scode.Substring(int.Parse(sxh.Substring(i, 1)), scode.Length - int.Parse(sxh.Substring(i, 1)));
                }
                else
                {
                    encoded = encoded + code.Substring(i, code.Length - i);
                    i = code.Length;
                }
            }
            //登录
            url = "https://jwxt.ncepu.edu.cn/Logon.do?method=logon";
            encoded = encoded.Replace("%", "%25");
            string postData = string.Format("userAccount=" + account + "&userPassword=" + password + "&encoded=" + encoded);
            GetUrlInfo(url, cookieContainer, "POST", true, postData);
            return cookieContainer;

        }

        private string GetUrlInfo(string url, CookieContainer cookieContainer, string method, bool AllowAutoRedirect, string postData = "")
        {
            HttpWebRequest req = (HttpWebRequest)WebRequest.Create(url);
            HttpWebResponse resp = new HttpWebResponse();
            ServicePointManager.ServerCertificateValidationCallback = (sender, certificate, chain, sslPolicyErrors) => true;
            try
            {
                req.Method = method;
                req.Accept = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9;";
                req.UserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3941.4 Safari/537.36";
                req.AllowAutoRedirect = AllowAutoRedirect;//服务端重定向。一般设置false
                req.CookieContainer = cookieContainer;
                req.KeepAlive = true;
                req.ContentType = "application/x-www-form-urlencoded;";
                req.AuthenticationLevel = System.Net.Security.AuthenticationLevel.None;

                req.ServerCertificateValidationCallback = (sender, certificate, chain, sslPolicyErrors) => { return true; };
                if (postData != "")
                {
                    #region 添加Post 参数            
                    byte[] postdatabyte = Encoding.GetEncoding("UTF-8").GetBytes(postData);
                    req.ContentLength = postdatabyte.Length;
                    using (Stream tstream = req.GetRequestStream())
                    {
                        tstream.Write(postdatabyte, 0, postdatabyte.Length);
                        tstream.Close();
                    }
                    #endregion
                }
                try
                {
                    resp = (HttpWebResponse)req.GetResponse();
                }
                catch (WebException e)
                {
                    if (e.Message.Contains("302"))
                    {
                        resp = (HttpWebResponse)((WebException)e).Response;
                    }
                }
                Stream stream = resp.GetResponseStream();
                //获取响应内容
                string ResponseGet = "";
                using (StreamReader reader = new StreamReader(stream, Encoding.UTF8))
                {
                    ResponseGet = reader.ReadToEnd();
                }
                return ResponseGet;
            }
            catch (Exception ex)
            {
                Console.WriteLine(DateTime.Now.ToString("MM-dd HH:mm") + ":" + ex.Message);
                return ex.Message;
            }
            finally
            {
                resp.Dispose();
            }
        }
        private string GetTime(string time = "")
        {
            if (time != String.Empty)
            {
                return time;
            }
            DateTime dateTime = DateTime.Now;
            int month = dateTime.Month;
            string result = "";
            if (month <= 8)
            {
                result = (dateTime.Year - 1) + "-" + dateTime.Year + "-2";
            }
            else
            {
                result = (dateTime.Year) + "-" + (dateTime.Year + 1) + "-1";
            }
            return result;
        }

        private string handleExamInfo(string examList)
        {
            string examPattern = "座位号</th></tr>(.*)</table>";
            Match resultExam = Regex.Match(examList, examPattern);
            string examData = resultExam.Groups[1].Value.ToString().Replace("style=\"background-color:#8b8b8b69;\"", "");
            string classExamPattern = "<tr><td>(.{0,4})</td><tdalign=\"left\"style=\"max-width:100px;word-wrap:break-word;word-break:break-all;white-space:normal;\">(.{0,10})</td><tdalign=\"left\">(.{7,9})</td><tdalign=\"left\">(.{0,20})</td><td>(.{0,30})</td><td>(.{0,20})</td><td>(.{0,8})</td><td>(.{0,8})</td><tdstyle=\"max-width:100px;word-wrap:break-word;word-break:break-all;white-space:normal;\">(.{0,200})</td><td>(.{0,5})</td></tr>";
            MatchCollection resultClassExam = Regex.Matches(examData, classExamPattern);
            StringBuilder tempExam = new StringBuilder("");
            for (int i = 0; i < resultClassExam.Count; i++)
            {
                tempExam.Append(resultClassExam[i].Groups[4].Value.ToString() + "\t");
                tempExam.Append(resultClassExam[i].Groups[5].Value.ToString() + "\t");
                tempExam.Append(resultClassExam[i].Groups[6].Value.ToString() + "\t");
                tempExam.Append("\r\n");
            }
            if (tempExam.Length == 0)
            {
                return "暂无考试信息";
            }
            return tempExam.ToString();
        }

        private string handleGradeInfo(string gradeList, bool isRank)
        {
            string gradePattern = ">课程性质</th></tr>(.*)</table></div><br/>";
            Match resultGrade = Regex.Match(gradeList, gradePattern);
            string gradeData = resultGrade.Groups[1].Value.ToString();
            gradeData = Regex.Replace(gradeData, "<ahref=\"javascript:openWindow\\((.{0,55})jx0404id=(\\d+)&(.{0,40})\\)\">", "jx0404id=$2;");
            gradeData = Regex.Replace(gradeData, "<ahref=\"javascript:openWindow(.{0,200})\">", "");
            gradeData = gradeData.Replace("color:red;", "");
            gradeData = gradeData.Replace("<tdstyle=\"color:#8B8B8B\">", "<tdstyle=\"\">");
            gradeData = gradeData.Replace("<!--控制成绩显示-->", "");
            gradeData = gradeData.Replace("<!--控制绩点显示-->", "");
            string classGradePattern = @"<tr><td>(.{1,2})</td><td>(.{10,20})</td><tdalign=""left"">(.{5,15})</td><tdalign=""left"">(.{0,30})</td><tdstyle="""">jx0404id=(\d+);(.{0,8})</a>(.{50,200})</tr>";
            MatchCollection resultClassGrade = Regex.Matches(gradeData, classGradePattern);
            StringBuilder tempGrade = new StringBuilder("");
            for (int i = 0; i < resultClassGrade.Count; i++)
            {
                tempGrade.Append(resultClassGrade[i].Groups[4].Value.ToString() + "\t");
                tempGrade.Append(resultClassGrade[i].Groups[6].Value.ToString() + "\t");
                if (isRank)
                {
                    string rank = GetGradeRank(resultClassGrade[i].Groups[5].Value.ToString(), resultClassGrade[i].Groups[6].Value.ToString());
                    if (!string.IsNullOrEmpty(rank))
                    {
                        tempGrade.Append(rank + "\t");
                    }
                }
                tempGrade.Append("\r\n");
            }
            if (tempGrade.Length == 0)
            {
                return "暂无成绩信息";
            }
            return tempGrade.ToString();
        }

        private List<Exam> ToExamArray(string examStr)
        {
            List<Exam> exams = new List<Exam>();
            string[] examArray = examStr.Split('|');
            for (int i = 0; i < examArray.Length - 1; i += 3)
            {
                Exam exam = new Exam();
                exam.ExamName = examArray[i];
                exam.ExamTime = examArray[i + 1];
                exam.ExamPosition = examArray[i + 2];
                exams.Add(exam);
            }
            return exams;
        }
        private List<Score> ToScoreArray(string scoreStr)
        {
            List<Score> scores = new List<Score>();
            string[] examArray = scoreStr.Split('|');
            for (int i = 0; i < examArray.Length - 1; i += 3)
            {
                Score score = new Score();
                score.ScoreTName = examArray[i];
                score.ScoreCourse = examArray[i + 1];
                score.ScoreValue = examArray[i + 2];
                scores.Add(score);
            }
            return scores;
        }
        private class Score : IEquatable<Score>
        {
            public string ScoreTName;
            public string ScoreCourse;
            public string ScoreValue;
            public bool Equals(Score score)
            {
                if (!this.ScoreTName.Equals(score.ScoreTName))
                    return false;
                if (!this.ScoreCourse.Equals(score.ScoreCourse))
                    return false;
                if (!this.ScoreValue.Equals(score.ScoreValue))
                    return false;
                return true;
            }

            public override int GetHashCode()
            {
                int hashName = ScoreTName == null ? 0 : ScoreTName.GetHashCode();
                int hashTime = ScoreCourse == null ? 0 : ScoreCourse.GetHashCode();
                int hashPosition = ScoreValue == null ? 0 : ScoreValue.GetHashCode();
                return hashName ^ hashTime ^ hashPosition;
            }
        }
        private class Exam : IEquatable<Exam>
        {
            public string ExamName;
            public string ExamTime;
            public string ExamPosition;
            public bool Equals(Exam exam)
            {
                if (!this.ExamName.Equals(exam.ExamName))
                    return false;
                if (!this.ExamTime.Equals(exam.ExamTime))
                    return false;
                if (!this.ExamPosition.Equals(exam.ExamPosition))
                    return false;
                return true;
            }

            public override int GetHashCode()
            {
                int hashName = ExamName == null ? 0 : ExamName.GetHashCode();
                int hashTime = ExamTime == null ? 0 : ExamTime.GetHashCode();
                int hashPosition = ExamPosition == null ? 0 : ExamPosition.GetHashCode();
                return hashName ^ hashTime ^ hashPosition;
            }
        }
    }
}
