using System;
using System.Collections.Generic;
using System.Text;
using System.Threading;
using net.sf.wts.client.proxyFiles;
using net.sf.wts.client.proxyFiles.GetChallenge;
using net.sf.wts.client.proxyFiles.Login;
using System.Security.Cryptography;
using net.sf.wts.client.proxyFiles.Upload;
using net.sf.wts.client.util;
using net.sf.wts.client.proxyFiles.Download;
using System.IO;
using net.sf.wts.client.proxyFiles.Start;
using net.sf.wts.client.proxyFiles.MonitorLogFile;
using net.sf.wts.client.proxyFiles.MonitorLogTail;
using net.sf.wts.client.proxyFiles.MonitorStatus;
using net.sf.wts.client.proxyFiles.CloseSession;
using net.sf.wts.client.proxyFiles.Stop;

namespace net.sf.wts.client
{
    public class WtsClient
    {
        private string url_;
        private GetChallengeService getChallengeService_ = null;
        private LoginService loginService_ = null;
        private UploadService UploadService_ = null;
        private DownloadService DownloadService_ = null;
        private MonitorLogFileService MonitorLogFileService_ = null;
        private MonitorLogTailService MonitorLogTailService_ = null;
        private MonitorStatusService MonitorStatusService_ = null;
        private StartService StartService_ = null;
        private CloseSessionService CloseSessionService_ = null;
        private StopService StopService_ = null;

        public WtsClient(string url)
        {
            url_ = url;
        }

        public string GetChallenge(string userName)
        {
            if (getChallengeService_ == null)
            {
                getChallengeService_ = new GetChallengeService();
                getChallengeService_.Url = url_+"GetChallenge";
            }

            return getChallengeService_.exec(userName);
        }
     
        public string Login(string userName, string challenge, string password, string serviceName)
        {
            if (loginService_ == null)
            {
                loginService_ = new LoginService();
                loginService_.Url = url_ + "Login";
            }

            string encrypted = EncodePassword(EncodePassword(password) + challenge);

            return loginService_.exec(userName, challenge, encrypted, serviceName);
        }

        public void Upload(string sessionTicket, string serviceName, string fileName, byte[] file)
        {
            if (UploadService_ == null)
            {
                UploadService_ = new UploadService();
                UploadService_.Url = url_ + "Upload";
            }

            UploadService_.exec(sessionTicket, serviceName, fileName, file);
        }

        public void Upload(string sessionTicket, string serviceName, string fileName, string fileLocalLocation)
        {
            if (UploadService_ == null)
            {
                UploadService_ = new UploadService();
                UploadService_.Url = url_ + "Upload";
            }

            byte[] array = FileUtils.ReadFileToArray(fileLocalLocation);

            UploadService_.exec(sessionTicket, serviceName, fileName, array);
        }

        public byte[] Download(string sessionTicket, string serviceName, string fileName)
        {
            if (DownloadService_ == null)
            {
                DownloadService_ = new DownloadService();
                DownloadService_.Url = url_ + "Download";
            }

            return DownloadService_.exec(sessionTicket, serviceName, fileName);
        }

        public void Download(string sessionTicket, string serviceName, string fileName, string fileToWrite)
        {
            byte[] array = Download(sessionTicket, serviceName, fileName);
            if (array != null)
            {
                FileUtils.writeByteArrayToFile(fileToWrite, array);
            }
        }

        public byte[] MonitorLogFile(String sessionTicket, String serviceName)
        {
            if (MonitorLogFileService_ == null)
            {
                MonitorLogFileService_ = new MonitorLogFileService();
                MonitorLogFileService_.Url = url_ + "MonitorLogFile";
            }

            return MonitorLogFileService_.exec(sessionTicket, serviceName);
        }
        
        public byte[] MonitorLogTail(String sessionTicket, String serviceName, int numberOfLines)
        {
            if (MonitorLogTailService_ == null)
            {
                MonitorLogTailService_ = new MonitorLogTailService();
                MonitorLogTailService_.Url = url_ + "MonitorLogTail";
            }

            return MonitorLogTailService_.exec(sessionTicket, serviceName, numberOfLines);
        }

        public String MonitorStatus(String sessionTicket, String serviceName)
        {
            if (MonitorStatusService_ == null)
            {
                MonitorStatusService_ = new MonitorStatusService();
                MonitorStatusService_.Url = url_ + "MonitorStatus";
            }

            return MonitorStatusService_.exec(sessionTicket, serviceName);
        }

        public void Start(string sessionTicket, string serviceName)
        {
            if (StartService_ == null)
            {
                StartService_ = new StartService();
                StartService_.Url = url_ + "Start";
            }

            StartService_.exec(sessionTicket, serviceName);
        }

        public void CloseSession(string sessionTicket, string serviceName)
        {
            if (CloseSessionService_ == null)
            {
                CloseSessionService_ = new CloseSessionService();
                CloseSessionService_.Url = url_ + "CloseSession";
            }

            CloseSessionService_.exec(sessionTicket, serviceName);
        }

        public void Stop(string sessionTicket, string serviceName)
        {
            if (StopService_ == null)
            {
                StopService_ = new StopService();
                StopService_.Url = url_ + "Stop";
            }

            StopService_.exec(sessionTicket, serviceName);
        }

        public string getUrl()
        {
            return url_;
        }

        private string EncodePassword(string originalPassword)
        {
            byte[] originalBytes;
            byte[] encodedBytes;
            MD5 md5;

            md5 = new MD5CryptoServiceProvider();
            UTF8Encoding enc = new UTF8Encoding();

            originalBytes = enc.GetBytes(originalPassword);
            encodedBytes = md5.ComputeHash(originalBytes);

            return Convert.ToBase64String(encodedBytes);
        }

        public static void Main(string[] args)
        {
            WtsClient wtsclient = new WtsClient("http://zolder:8080/wts/services/");
            string challenge = wtsclient.GetChallenge("kdforc0");
            Console.WriteLine(challenge);
            string ticket = wtsclient.Login("kdforc0", challenge, "Vitabis1", "regadb-hiv-type");
            Console.WriteLine(ticket);
            wtsclient.Upload(ticket, "regadb-hiv-type", "nt_sequence", "C:\\jvsant1\\isolate.fasta");
            wtsclient.Start(ticket, "regadb-hiv-type");

            Thread.Sleep(5000);

            Console.WriteLine("monitor log file:");
            string logFile = System.Text.Encoding.UTF8.GetString(wtsclient.MonitorLogFile(ticket, "regadb-hiv-type"));
            Console.WriteLine(logFile);

            Console.WriteLine("monitor log tail:");
            string logTail = System.Text.Encoding.UTF8.GetString(wtsclient.MonitorLogTail(ticket, "regadb-hiv-type", 10));
            Console.WriteLine(logTail);

            Console.WriteLine("status:" + wtsclient.MonitorStatus(ticket, "regadb-hiv-type"));
            wtsclient.Download(ticket, "regadb-hiv-type", "type", "C:\\jvsant1\\type.txt");

            wtsclient.CloseSession(ticket, "regadb-hiv-type");

            Console.ReadLine();
        }
    }
}
