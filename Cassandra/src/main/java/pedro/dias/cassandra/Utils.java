/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pedro.dias.cassandra;

import com.datastax.driver.core.LocalDate;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author paafonso
 */
public class Utils {

    public static final Pattern PATTERN_NUMERO_PROCESSO = Pattern.compile("^[\\d]{1,15}[a-zA-Z]{1}$");
    public static final Pattern PATTERN_BI = Pattern.compile("^[\\d]{1,8}");
    public static final SimpleDateFormat ENTRY_DATE_DAY_FORMAT = new SimpleDateFormat("yyy-MM-dd");
    
    public static String getLocalHostLANAddress() {
        try {
            InetAddress candidateAddress = null;
            // Iterate all NICs (network interface cards)...
            for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();) {
                NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
                // Iterate all IP addresses assigned to each card...
                for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements();) {
                    InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
                    if (!inetAddr.isLoopbackAddress()) {

                        if (inetAddr.isSiteLocalAddress()) {
                            // Found non-loopback site-local address. Return it immediately...

                            if (inetAddr.getHostAddress().matches("[0-9]{1,3}\\.{1}[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}")) {
                                return inetAddr.getHostAddress();
                            }
                        } else if (candidateAddress == null) {
                            // Found non-loopback address, but not necessarily site-local.
                            // Store it as a candidate to be returned if site-local address is not subsequently found...
                            candidateAddress = inetAddr;
                            // Note that we don't repeatedly assign non-loopback non-site-local addresses as candidates,
                            // only the first. For subsequent iterations, candidate will be non-null.
                        }
                    }
                }
            }
            if (candidateAddress != null) {
                // We did not find a site-local address, but we found some other non-loopback address.
                // Server might have a non-site-local address assigned to its NIC (or it might be running
                // IPv6 which deprecates the "site-local" concept).
                // Return this non-loopback candidate address...
                return candidateAddress.getHostAddress();
            }
            // At this point, we did not find a non-loopback address.
            // Fall back to returning whatever InetAddress.getLocalHost() returns...
            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            if (jdkSuppliedAddress == null) {
                throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
            }
            return jdkSuppliedAddress.getHostAddress();
        } catch (Exception e) {
            UnknownHostException unknownHostException = new UnknownHostException("Failed to determine LAN address: " + e);
            unknownHostException.initCause(e);
            return null;
        }
    }

    public static String toStringArray(Object[] params) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (Object obj : params) {
            if (obj != null) {
                sb.append(obj.toString());
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public static Date getDateFrom(Calendar cal) {
        return cal.getTime();
    }

    public static LocalDate getLocalDateFrom(Calendar cal) {
        return LocalDate.fromYearMonthDay(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
    }

    public static int getHourFrom(Calendar cal) {
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    public static String mapToString(HashMap<String, String> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (String key : map.keySet()) {
            sb.append("'").append(key).append("'").append("=");
            sb.append("'").append(map.get(key)).append("'").append(",");
        }
        String result = sb.toString();
        return result.substring(0, result.length() - 1).concat("}");
    }

    public static List<Class> getClasses(String packageName) throws Exception {
        List<Class> listaClasses = new ArrayList<>();
        ClassLoader classLoader = Utils.class.getClassLoader();
        for (ClassInfo classInfo : ClassPath.from(classLoader).getTopLevelClasses(packageName)) {
            String className = classInfo.getName();
            listaClasses.add(Class.forName(className));        }
        return listaClasses;
    }
}
