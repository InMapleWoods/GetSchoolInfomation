using System;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Text;
using System.Text.RegularExpressions;

namespace GetInformation
{
    public class GetStudentGradeOrExam
    {
        public string GetStudentGrade()
        {
            CookieContainer cookieContainer = getInfo("201709001013", "llf99723");
            //获取成绩
            string url = "https://jwxt.ncepu.edu.cn/jsxsd/newxspj/zhxspj_list.do";
            string postData = "cj0701id=&xnxq=" + GetTime() + "&pageIndex=1";
            string ResponseGet = GetUrlInfo(url, cookieContainer, "POST", false, postData);
            string gradeList = ResponseGet.Replace("\r\n", "").Replace(" ", "").Replace("\t", "").Replace("&nbsp;","");
            string score = handleGradeInfo(gradeList);
            return score;
        }

        public string GetStudentExam()
        {
            CookieContainer cookieContainer = getInfo("201709001013", "llf99723");
            //获取考试
            string url = "https://jwxt.ncepu.edu.cn/jsxsd/xsks/xsksap_list";
            string postData = "xqlbmc=&xnxqid=" + GetTime() + "&kc=&ksjs=&jkls=";
            string ResponseGet = GetUrlInfo(url, cookieContainer, "POST", false, postData);
            string examList = ResponseGet.Replace("\r\n", "").Replace(" ", "").Replace("\t", "").Replace("&nbsp;", "");
            string exam = handleExamInfo(examList);
            return exam;
        }

        private CookieContainer getInfo(string account, string password)
        {
            HttpWebRequest req;
            HttpWebResponse resp = new HttpWebResponse();
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
            req.Method = method;
            req.Accept = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9;";
            req.UserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3941.4 Safari/537.36";
            req.AllowAutoRedirect = AllowAutoRedirect;//服务端重定向。一般设置false
            req.CookieContainer = cookieContainer;
            req.KeepAlive = true;
            req.ContentType = "application/x-www-form-urlencoded;";
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
            catch (Exception ex)
            {
                Console.WriteLine(DateTime.Now.ToString("MM-dd HH:mm") + ":" + ex.Message);
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
        private string GetTime()
        {
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
            string classExamPattern = "<tr><td>(.{0,4})</td><tdalign=\"left\"style=\"max-width:100px;word-wrap:break-word;word-break:break-all;white-space:normal;\">(.{0,10})</td><tdalign=\"left\">(.{7,9})</td><tdalign=\"left\">(.{0,20})</td><td>(.{0,30})</td><td>(.{0,20})</td><td>(.{0,8})</td><td></td><tdstyle=\"max-width:100px;word-wrap:break-word;word-break:break-all;white-space:normal;\"></td><td></td></tr>";
            MatchCollection resultClassExam = Regex.Matches(examData, classExamPattern);
            StringBuilder tempExam = new StringBuilder("");
            for (int i = 0; i < resultClassExam.Count; i++)
            {
                tempExam.Append(resultClassExam[i].Groups[4].Value.ToString() + "\t");
                tempExam.Append(resultClassExam[i].Groups[5].Value.ToString() + "\t");
                tempExam.Append(resultClassExam[i].Groups[6].Value.ToString() + "\t");
            }
            if(tempExam.Length==0)
            {
                return "暂无考试信息";
            }
            return tempExam.ToString();
        }

        private string handleGradeInfo(string gradeList)
        {
            string gradePattern = ">操作</th></tr>(.*)</table><divid=\"PagingControl1";
            Match resultGrade = Regex.Match(gradeList, gradePattern);
            string gradeData = resultGrade.Groups[1].Value.ToString();
            gradeData = gradeData.Replace("<ahref=\"javascript:void(0);\"style=\"opacity:0.2\">未录入</a>", "未录入");
            gradeData = Regex.Replace(gradeData, "<ahref=(.{0,200})排名</a>", "");
            gradeData = Regex.Replace(gradeData, "<ahref=(.{0,200})留言</a>", "留言");
            string classGradePattern = "<tr><td>(.{1,2})</td><td>(.{7,12})</td><td>(.{2,4})</td><td>(.{1,20})</td><td>(.{7,12})</td><td>(.{0,2})</td><td>(.{1,25})</td><td>(.{1,5})</td><td>(.{0,5})</td><td></td><td>(.{0,5})</td><td>(.{0,5})</td><td></td><td>留言</td></tr>";
            
            MatchCollection resultClassGrade = Regex.Matches(gradeData, classGradePattern);
            StringBuilder tempGrade = new StringBuilder("");
            for (int i = 0; i < resultClassGrade.Count; i++)
            {
                tempGrade.Append(resultClassGrade[i].Groups[3].Value.ToString() + "\t");
                tempGrade.Append(resultClassGrade[i].Groups[7].Value.ToString() + "\t");
                tempGrade.Append(resultClassGrade[i].Groups[10].Value.ToString() + "\t");
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
