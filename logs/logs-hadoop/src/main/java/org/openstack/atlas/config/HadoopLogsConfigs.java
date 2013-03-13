package org.openstack.atlas.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.openstack.atlas.config.LbLogsConfiguration;
import org.openstack.atlas.config.LbLogsConfigurationKeys;
import org.openstack.atlas.logs.hadoop.jobs.HadoopJob;
import org.openstack.atlas.util.HdfsUtils;
import org.openstack.atlas.util.StaticFileUtils;

public class HadoopLogsConfigs {

    private static final Log LOG = LogFactory.getLog(HadoopLogsConfigs.class);
    protected static final String cacheDir;
    protected static final String backupDir;
    protected static final String fileSystemRootDir;
    protected static final String localJobsJarPath;
    protected static final String hadoopXmlFile;
    protected static final String mapreduceInputPrefix;
    protected static final String mapreduceOutputPrefix;
    protected static final String fileRegion;
    protected static final String hdfsUserName;
    private static final String numReducers;
    protected static String hdfsJobsJarPath;
    protected static Configuration hadoopConfiguration = null;
    protected static HdfsUtils hdfsUtils = null;
    protected static boolean jarCopyed = false;

    static {
        LbLogsConfiguration lbLogsConf = new LbLogsConfiguration();
        cacheDir = lbLogsConf.getString(LbLogsConfigurationKeys.rawlogs_cache_dir);
        backupDir = lbLogsConf.getString(LbLogsConfigurationKeys.rawlogs_backup_dir);
        fileSystemRootDir = lbLogsConf.getString(LbLogsConfigurationKeys.filesystem_root_dir);
        localJobsJarPath = lbLogsConf.getString(LbLogsConfigurationKeys.job_jar_path);
        hadoopXmlFile = lbLogsConf.getString(LbLogsConfigurationKeys.hadoop_xml_file);
        mapreduceInputPrefix = lbLogsConf.getString(LbLogsConfigurationKeys.mapreduce_input_prefix);
        mapreduceOutputPrefix = lbLogsConf.getString(LbLogsConfigurationKeys.mapreduce_output_prefix);
        fileRegion = lbLogsConf.getString(LbLogsConfigurationKeys.files_region);
        hdfsUserName = lbLogsConf.getString(LbLogsConfigurationKeys.hdfs_user_name);
        hdfsJobsJarPath = lbLogsConf.getString(LbLogsConfigurationKeys.hdfs_job_jar_path);
        numReducers = lbLogsConf.getString(LbLogsConfigurationKeys.num_reducers);
    }

    public static HadoopJob getHadoopJob(Class<? extends HadoopJob> jobClass) {
        HadoopJob implementation = null;
        try {
            implementation = jobClass.newInstance();
        } catch (InstantiationException ex) {
            String msg = String.format("Could not instantiate class %s", jobClass.getName());
            LOG.error(msg, ex);
            throw new IllegalArgumentException(msg, ex);
        } catch (IllegalAccessException ex) {
            String msg = String.format("Could not instantiate class %s", jobClass.getName());
            LOG.error(msg, ex);
            throw new IllegalArgumentException(msg, ex);
        }
        implementation.setConfiguration(getHadoopConfiguration());
        return implementation;
    }

    public static String staticToString() {
        StringBuilder sb = new StringBuilder();
        sb = sb.append("{cacheDir=").append(cacheDir).
                append(", backupdir=").append(backupDir).
                append(", fileSystemRootDir=").append(fileSystemRootDir).
                append(", hadoopXmlFile=").append(hadoopXmlFile).
                append(", mapreduceInputPrefix=").append(mapreduceInputPrefix).
                append(", mapreduceOutputPrefix=").append(mapreduceOutputPrefix).
                append(", fileRegion=").append(fileRegion).
                append(", hdfsUserName=").append(hdfsUserName).
                append(", jarCopyed=").append(jarCopyed).
                append("}");
        return sb.toString();
    }

    public static Configuration getHadoopConfiguration() {
        if (hadoopConfiguration == null) {
            hadoopConfiguration = new Configuration();
            hadoopConfiguration.addResource(new Path(StaticFileUtils.expandUser(hadoopXmlFile)));
        }
        return hadoopConfiguration;
    }

    public static HdfsUtils getHdfsUtils() {
        if (hdfsUtils == null) {
            hdfsUtils = new HdfsUtils();
            try {
                hdfsUtils.setConf(getHadoopConfiguration());
                hdfsUtils.setUser(hdfsUserName);
                hdfsUtils.init();
            } catch (IOException ex) {
                hdfsUtils = null;
                throw new IllegalStateException("Could not initialize HadoopLogsConfigs class", ex);
            } catch (InterruptedException ex) {
                hdfsUtils = null;
                throw new IllegalStateException("Could not initialize HadoopLogsConfigs class", ex);
            }
        }
        return hdfsUtils;
    }

    public static String getCacheDir() {
        return cacheDir;
    }

    public static String getBackupDir() {
        return backupDir;
    }

    public static String getFileSystemRootDir() {
        return fileSystemRootDir;
    }

    public static String getHadoopXmlFile() {
        return hadoopXmlFile;
    }

    public static String getMapreduceInputPrefix() {
        return mapreduceInputPrefix;
    }

    public static String getMapreduceOutputPrefix() {
        return mapreduceOutputPrefix;
    }

    public static String getFileRegion() {
        return fileRegion;
    }

    public static String getHdfsUserName() {
        return hdfsUserName;
    }

    public static String getHdfsJobsJarPath() {
        return hdfsJobsJarPath;
    }

    public static String getLocalJobsJarPath() {
        return localJobsJarPath;
    }

    public static boolean isJarCopyed() {
        return jarCopyed;
    }

    public static void copyJobsJar() throws FileNotFoundException, IOException {
        if (!jarCopyed) { // If this is the first run since the app was deployed then copy the jobs jar
            LOG.info(String.format("Copying jobsJar %s -> %s", localJobsJarPath, hdfsJobsJarPath));
            InputStream is = StaticFileUtils.openInputFile(localJobsJarPath);
            FSDataOutputStream os = hdfsUtils.openHdfsOutputFile(hdfsJobsJarPath, false, true);
            StaticFileUtils.copyStreams(is, os, null, hdfsUtils.getBufferSize());
            StaticFileUtils.close(is);
            StaticFileUtils.close(os);
            jarCopyed = true;
        } else {
            LOG.info("JobsJar already copyed not copying again.");
        }
    }

    public static String getNumReducers() {
        return numReducers;
    }
}