#include <string.h>
#include <jni.h>
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>

#include <sys/resource.h>
#include <dirent.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <sys/wait.h>
#include <pthread.h>

#define PROC_DIRECTORY "/proc/"
#define CASE_SENSITIVE    1
#define CASE_INSENSITIVE  0
#define EXACT_MATCH       1
#define INEXACT_MATCH     0
#define MAX_LINE_LEN 5

#include <android/log.h>
#define  TAG    "daemon"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG,__VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG,__VA_ARGS__)
#define LOG "Helper"

void thread(char* srvname) {
	while(1){
		check_and_restart_service(srvname);
		sleep(60);
	}
}

char* a;

/**
 * srvname  服务名
 * sd 之前创建子进程的pid写入的文件路径
 */
int start(int argc, char* srvname, char* sd) {
	pthread_t id;
	int ret;
	struct rlimit r;

	/**
	 * 第一次fork的作用是让shell认为本条命令已经终止，不用挂在终端输入上。
	 * 还有一个作用是为后面setsid服务。setsid的调用者不能是进程组组长(group leader)。
	 * 此时父进程是进程组组长。
	 */
	int pid = fork();
	LOGI("fork pid: %d", pid);
	if (pid < 0) {
		LOGI("first fork() error pid %d,so exit", pid);
		exit(0);
	} else if (pid != 0) {
		LOGI("first fork(): I'am father pid=%d", getpid());
		//exit(0);
	} else { //  第一个子进程
		LOGI("first fork(): I'am child pid=%d", getpid());
		//int setsid = setsid();
		LOGI("first fork(): setsid=%d", setsid());
		umask(0); //使用umask修改文件的屏蔽字，为文件赋予跟多的权限，因为继承来的文件可能某些权限被屏蔽，从而失去某些功能，如读写

		int pid = fork();
		if (pid == 0) { // 第二个子进程
			FILE  *fp;
			sprintf(sd,"%s/pid",sd);
			if((fp=fopen(sd,"a"))==NULL) {//打开文件 没有就创建
				LOGI("%s文件还未创建!",sd);
				ftruncate(fp, 0);
				lseek(fp, 0, SEEK_SET);
			}
			fclose(fp);
			fp=fopen(sd,"rw");
			if(fp>0){
				char buff1[6];
				int p = 0;
				memset(buff1,0,sizeof(buff1));
				fseek(fp,0,SEEK_SET);
				fgets(buff1,6,fp);  //读取一行
				LOGI("读取的进程号：%s",buff1);
				if(strlen(buff1)>1){ // 有值

					kill(atoi(buff1), SIGTERM);
					LOGI("杀死进程，pid=%d",atoi(buff1));
				}
			}
			fclose(fp);
			fp=fopen(sd,"w");
			char buff[100];
			int k = 3;
			if(fp>0){
				sprintf(buff,"%lu",getpid());
				fprintf(fp,"%s\n",buff); // 把进程号写入文件
				LOGI("写入。。。。。");
			}
			fclose(fp);
			fflush(fp);

			LOGI("step 2 I'am child-child pid=%d", getpid());
			//step 4:修改进程工作目录为根目录，chdir(“/”).
			chdir("/");
			//step 5:关闭不需要的从父进程继承过来的文件描述符。
			if (r.rlim_max == RLIM_INFINITY) {
				r.rlim_max = 1024;
			}
			int i;
			for (i = 0; i < r.rlim_max; i++) {
				close(i);
			}

			umask(0);
			ret = pthread_create(&id, NULL, (void *) thread, srvname);
			if (ret != 0) {
				printf("Create pthread error!\n");
				exit(1);
			}
			int stdfd = open ("/dev/null", O_RDWR);
			dup2(stdfd, STDOUT_FILENO);
			dup2(stdfd, STDERR_FILENO);
		} else {
			exit(0);
		}
	}
	return 0;
}

/**
 * 执行命令
 */
void ExecuteCommandWithPopen(char* command, char* out_result,
		int resultBufferSize) {
	FILE * fp;
	out_result[resultBufferSize - 1] = '\0';
	fp = popen(command, "r");
	if (fp) {
		fgets(out_result, resultBufferSize - 1, fp);
		out_result[resultBufferSize - 1] = '\0';
		pclose(fp);
	} else {
		LOGI("popen null,so exit");
		exit(0);
	}
}

/**
 * 检测服务，如果不存在服务则启动.
 * 通过am命令启动一个laucher服务,由laucher服务负责进行主服务的检测,laucher服务在检测后自动退出
 */
void check_and_restart_service(char* service) {
	LOGI("当前所在的进程pid=",getpid());
	char cmdline[200];
	sprintf(cmdline, "am startservice --user 0 -n %s", service);
	char tmp[200];
	sprintf(tmp, "cmd=%s", cmdline);
	ExecuteCommandWithPopen(cmdline, tmp, 200);
	LOGI( tmp, LOG);
}

jstring stoJstring(JNIEnv* env, const char* pat) {
	jclass strClass = (*env)->FindClass(env, "Ljava/lang/String;");
	jmethodID ctorID = (*env)->GetMethodID(env, strClass, "<init>",
			"([BLjava/lang/String;)V");
	jbyteArray bytes = (*env)->NewByteArray(env, strlen(pat));
	(*env)->SetByteArrayRegion(env, bytes, 0, strlen(pat), (jbyte*) pat);
	jstring encoding = (*env)->NewStringUTF(env, "utf-8");
	return (jstring)(*env)->NewObject(env, strClass, ctorID, bytes, encoding);
}


/**
 * 判断是否是数字
 */
int IsNumeric(const char* ccharptr_CharacterList) {
	//LOGI("IsNumeric: test.cpp/main: argc=%d",ccharptr_CharacterList);
	for (; *ccharptr_CharacterList; ccharptr_CharacterList++)
		if (*ccharptr_CharacterList < '0' || *ccharptr_CharacterList > '9')
			return 0; // false
	return 1; // true
}

//intCaseSensitive=0大小写不敏感
int strcmp_Wrapper(const char *s1, const char *s2, int intCaseSensitive) {
	if (intCaseSensitive)
		return !strcmp(s1, s2);
	else
		return !strcasecmp(s1, s2);
}

//intCaseSensitive=0大小写不敏感
int strstr_Wrapper(const char* haystack, const char* needle,
		int intCaseSensitive) {
	if (intCaseSensitive)
		return (int) strstr(haystack, needle);
	else
		return (int) strcasestr(haystack, needle);
}

/**
 * 通过进程名称获取pid
 */
pid_t GetPIDbyName_implements(const char* cchrptr_ProcessName,
		int intCaseSensitiveness, int intExactMatch) {

	char chrarry_CommandLinePath[100];
	char chrarry_NameOfProcess[300];
	char* chrptr_StringToCompare = NULL;
	pid_t pid_ProcessIdentifier = (pid_t) - 1;
	struct dirent* de_DirEntity = NULL;
	DIR* dir_proc = NULL;

	int (*CompareFunction)(const char*, const char*, int);

	if (intExactMatch)
		CompareFunction = &strcmp_Wrapper;
	else
		CompareFunction = &strstr_Wrapper;

	dir_proc = opendir(PROC_DIRECTORY);
	if (dir_proc == NULL) {
		perror("Couldn't open the " PROC_DIRECTORY " directory");
		return (pid_t) - 2;
	}

	while ((de_DirEntity = readdir(dir_proc))) {
		if (de_DirEntity->d_type == DT_DIR) {

			if (IsNumeric(de_DirEntity->d_name)) {
				strcpy(chrarry_CommandLinePath, PROC_DIRECTORY);
				strcat(chrarry_CommandLinePath, de_DirEntity->d_name);
				strcat(chrarry_CommandLinePath, "/cmdline");
				FILE* fd_CmdLineFile = fopen(chrarry_CommandLinePath, "rt"); //open the file for reading text
				if (fd_CmdLineFile) {
					LOGI("chrarry_NameOfProcess %s", chrarry_NameOfProcess);
					fscanf(fd_CmdLineFile, "%s", chrarry_NameOfProcess); //read from /proc/<NR>/cmdline
					fclose(fd_CmdLineFile); //close the file prior to exiting the routine

					chrptr_StringToCompare = chrarry_NameOfProcess;
					if (CompareFunction(chrptr_StringToCompare,
							cchrptr_ProcessName, intCaseSensitiveness)) {
						pid_ProcessIdentifier = (pid_t) atoi(
								de_DirEntity->d_name);
						LOGI("processName=%d, pid=%d",cchrptr_ProcessName,pid_ProcessIdentifier);
						closedir(dir_proc);
						return pid_ProcessIdentifier;
					}
				}
			}
		}
	}
	LOGI("processName=%d, pid=%d",cchrptr_ProcessName,pid_ProcessIdentifier);
	closedir(dir_proc);
	return pid_ProcessIdentifier;
}

/**
 * 检测服务，如果不存在服务则启动
 */
void check_and_restart_activity(char* service) {

	char cmdline[200];
	sprintf(cmdline, "am start -n %s", service);
	char tmp[200];
	sprintf(tmp, "cmd=%s", cmdline);
	ExecuteCommandWithPopen(cmdline, tmp, 200);
	LOGI( tmp, LOG);
}

/**
 * 返回ABI给Java
 */
jstring Java_com_allever_social_fork_NativeRuntime_stringFromJNI(JNIEnv* env, jobject thiz) {
	#if defined(__arm__)
		#if defined(__ARM_ARCH_7A__)
			#if defined(__ARM_NEON__)
				#define ABI "armeabi-v7a/NEON"
			#else
				#define ABI "armeabi-v7a"
			#endif
		#else
			#define ABI "armeabi"
		#endif
	#elif defined(__i386__)
		#define ABI "x86"
	#elif defined(__mips__)
		#define ABI "mips"
	#else
		#define ABI "unknown"
	#endif
	return (*env)->NewStringUTF(env,
			"Hello from JNI !  Compiled with ABI " ABI ".");
}

/**
 * jstring 转 String
 */
char* jstringTostring(JNIEnv* env, jstring jstr) {
	char* rtn = NULL;
	jclass clsstring = (*env)->FindClass(env, "java/lang/String");
	jstring strencode = (*env)->NewStringUTF(env, "utf-8");
	jmethodID mid = (*env)->GetMethodID(env, clsstring, "getBytes",
			"(Ljava/lang/String;)[B");
	jbyteArray barr = (jbyteArray)(*env)->CallObjectMethod(env, jstr, mid,
			strencode);
	jsize alen = (*env)->GetArrayLength(env, barr);
	jbyte* ba = (*env)->GetByteArrayElements(env, barr, JNI_FALSE);
	if (alen > 0) {
		rtn = (char*) malloc(alen + 1);
		memcpy(rtn, ba, alen);
		rtn[alen] = 0;
	}
	(*env)->ReleaseByteArrayElements(env, barr, ba, 0);
	return rtn;
}


/**
 * 查找进程
 */
pid_t JNICALL Java_com_allever_social_fork_NativeRuntime_findProcess(JNIEnv* env,
		jobject thiz, jstring cchrptr_ProcessName) {
	char * rtn = jstringTostring(env, cchrptr_ProcessName);
	LOGI("Java_com_yyh_fork_NativeRuntime_findProcess run....ProcessName:%s", rtn);
	//return 1;
	return GetPIDbyName_implements(rtn, 0, 0); //大小写不敏感 sub串匹配
}

/**
 * 启动Service
 */
void Java_com_allever_social_foke_NativeRuntime_startService(JNIEnv* env, jobject thiz,
		jstring cchrptr_ProcessName, jstring sdpath) {
	char * rtn = jstringTostring(env, cchrptr_ProcessName); // 得到进程名称
	char * sd = jstringTostring(env, sdpath);
	LOGI("Java_com_yyh_fork_NativeRuntime_startService run....ProcessName:%s", rtn);
	a = rtn;
	start(1, rtn, sd);
}

/**
 * 关闭Service
 */
void Java_com_allever_social_fork_NativeRuntime_stopService() {
	exit(0);
}

/**
 * 启动Activity
 */
void Java_com_allever_social_fork_NativeRuntime_startActivity(JNIEnv* env, jobject thiz,
		jstring activityName) {
	char * rtn = jstringTostring(env, activityName);
	LOGI("check_and_restart_activity run....activityName:%s", rtn);
	check_and_restart_activity(rtn);
}

JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* reserved) {
	JNIEnv* env = NULL;
	jint result = -1;

	if ((*vm)->GetEnv(vm, (void**) &env, JNI_VERSION_1_4) != JNI_OK) {
		return result;
	}
	LOGI("JNI_OnLoad ......");
	return JNI_VERSION_1_4;
}
