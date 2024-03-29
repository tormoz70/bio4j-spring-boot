package ru.bio4j.spring.commons.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import ru.bio4j.spring.commons.converter.Converter;
import ru.bio4j.spring.commons.converter.MetaTypeConverter;
import ru.bio4j.spring.model.transport.*;
import ru.bio4j.spring.model.transport.jstore.Sort;
import ru.bio4j.spring.model.transport.jstore.filter.Expression;
import ru.bio4j.spring.model.transport.jstore.filter.Filter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

import static ru.bio4j.spring.model.transport.jstore.filter.FilterBuilder.*;

public class Utl {
    private static final Logger LOG = LoggerFactory.getLogger(Utl.class);


    /**
     * Вытаскивает имя вайла из полного пути
     *
     * @param filePath
     * @return
     */
    public static String fileName(String filePath) {
        if (Strings.isNullOrEmpty(filePath)) {
            int p = filePath.lastIndexOf(File.separator);
            if (p >= 0)
                return filePath.substring(p + 1);
            return filePath;
        }
        return filePath;
    }

    /**
     * Вытаскивает расширение файла из полного пути
     *
     * @param fileName
     * @return
     */
    public static String fileNameExt(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        int p = fileName.lastIndexOf(File.separator);

        if (i > p) {
            extension = fileName.substring(i + 1);
        }
        return extension;
    }

    public static String fileNameWithoutExt(String fileName) {
        if (Strings.isNullOrEmpty(fileName))
            return fileName;
        return fileName.replaceFirst("[.][^.]+$", "");
    }

    /**
     * Приводит LongToInt, если это возможно
     *
     * @param l
     * @return
     */
    public static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (l + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }

    /**
     * Читает из потока заданное кол-во байтов и возвращает их в виде массива
     *
     * @param in
     * @param length
     * @return
     * @throws IOException
     */
    public static byte[] readStream(InputStream in, int length) {
        try {
            if (length > (Integer.MAX_VALUE - 5))
                throw new IllegalArgumentException("Parameter \"length\" too big!");
            byte[] buff = new byte[Utl.safeLongToInt(length)];
            int readed = in.read(buff);
            return buff;
        } catch (IOException e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    /**
     * @param in
     * @return
     * @throws IOException
     */
    public static String readStream(InputStream in, String encoding, boolean addLineSeparator) {
        try {
            InputStreamReader is = new InputStreamReader(in, encoding);
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(is);
            String read = br.readLine();
            while (read != null) {
                sb.append(read);
                if (addLineSeparator)
                    sb.append(System.lineSeparator());
                read = br.readLine();
            }
            return sb.toString();
        } catch (IOException e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public static String readStream(InputStream in, String encoding) {
        return readStream(in, encoding, true);
    }

    public static String readStream(InputStream in) {
        return readStream(in, "UTF-8");
    }


    /**
     * @param clazz
     * @param in
     * @return
     * @throws JAXBException
     */
    public static <T> T unmarshalXml(Class<T> clazz, InputStream in) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Object obj = jaxbUnmarshaller.unmarshal(in);
            if (obj == null) return null;
            if (obj.getClass() == clazz)
                return (T) obj;
            return null;
        } catch (JAXBException e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    /**
     * Checks two classes is assignable
     *
     * @param clazz1
     * @param clazz2
     * @return boolean
     * @throws
     */
    public static boolean typesIsAssignable(Class<?> clazz1, Class<?> clazz2) {
        if ((clazz1 == null) && (clazz2 == null)) return true;
        if (((clazz1 != null) && (clazz2 == null)) || ((clazz1 == null) && (clazz2 != null))) return false;
        return (clazz1 == clazz2) || clazz1.isAssignableFrom(clazz2) || clazz2.isAssignableFrom(clazz1);
    }

    /**
     * Checks two classes is the same
     *
     * @param clazz1
     * @param clazz2
     * @return boolean
     * @throws
     */
    public static boolean typesIsSame(Class<?> clazz1, Class<?> clazz2) {
        if ((clazz1 == null) || (clazz2 == null)) return true;
        if (((clazz1 != null) && (clazz2 == null)) || ((clazz1 == null) && (clazz2 != null))) return false;
        return clazz1.getName().equals(clazz2.getName());
    }


    public static boolean typeHasInterface(Class<?> clazz, Class<?> serviceInterface) {
        Class[] interfaces = clazz.getInterfaces();
        for (Class i : interfaces) {
            if (i.toString().equals(serviceInterface.toString()))
                return true;
        }
        return false;
    }

    /**
     * @param annotationType - Type of annotation to find
     * @param clazz          - Annotated type
     * @return - Annotation object
     */
    public static <T extends Annotation> T findAnnotation(Class<T> annotationType, Class<?> clazz) {
        for (Annotation annotation : clazz.getDeclaredAnnotations()) {
            Class<?> atype = annotation.annotationType();
            if (typesIsAssignable(atype, annotationType))
                return (T) annotation;
        }
        return null;
    }

    public static <T extends Annotation> T findAnnotation(Class<T> annotationType, Field field) {
        for (Annotation annotation : field.getDeclaredAnnotations()) {
            Class<?> atype = annotation.annotationType();
            if (typesIsAssignable(atype, annotationType))
                return (T) annotation;
        }
        return null;
    }


    /**
     * @param packageName
     * @return
     */
    public static String pkg2path(String packageName) {
        return (Strings.isNullOrEmpty(packageName) ? null : "/" + packageName.replace('.', '/') + "/");
    }

    /**
     * @param path
     * @return
     */
    public static String path2pkg(String path) {
        if (Strings.isNullOrEmpty(path)) return null;
        String result = path.replace('/', '.');
        if (result.charAt(0) == '.')
            result = result.substring(1);
        if (result.charAt(result.length() - 1) == '.')
            result = result.substring(0, result.length() - 1);
        return result;
    }

    /**
     * @param path
     * @return
     */
    public static String classNameFromPath(String path) {
        if (Strings.isNullOrEmpty(path)) return null;
        return path.endsWith(".class") ? path2pkg(path.replaceAll("\\.class$", "")) : null;
    }


    private static void extractAllObjectFields(List<Field> fields, Class<?> type) {
        for (Field field : type.getDeclaredFields())
            fields.add(field);
        if (type.getSuperclass() != null)
            extractAllObjectFields(fields, type.getSuperclass());
    }

    public static List<Field> getAllObjectFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        extractAllObjectFields(fields, type);
        return fields;
    }

    public static String fieldValueAsString(Object bean, java.lang.reflect.Field field) {
        Object val;
        try {
            field.setAccessible(true);
            val = field.get(bean);
        } catch (IllegalAccessException ex) {
            throw new AccessToBeanFieldException(ex);
        }
        String valStr = (val instanceof String) ? ((String) val).trim() : null;
        if (!Strings.isNullOrEmpty(valStr) && valStr.indexOf("\n") >= 0) {
            return valStr.substring(0, valStr.indexOf("\n")) + "...";
        } else
            return "" + val;
    }

    public static Object fieldValueAsObject(Object bean, java.lang.reflect.Field field) {
        Object val;
        try {
            field.setAccessible(true);
            val = field.get(bean);
        } catch (IllegalAccessException ex) {
            throw new AccessToBeanFieldException(ex);
        }
        return val;
    }

    public static Object fieldValueAsObject(Object bean, String fieldName) {
        Object val = null;
        if (bean != null) {
            java.lang.reflect.Field field = findFieldOfBean(bean.getClass(), fieldName);
            if (field != null) {
                try {
                    field.setAccessible(true);
                    val = field.get(bean);
                } catch (IllegalAccessException ex) {
                    throw new AccessToBeanFieldException(ex);
                }
            }
        }
        return val;
    }

    public static <T> T fieldValue(Object bean, String fieldName, Class<T> clazz) {
        T val = null;
        if (bean != null) {
            java.lang.reflect.Field field = findFieldOfBean(bean.getClass(), fieldName);
            if (field != null) {
                try {
                    field.setAccessible(true);
                    val = Converter.toType(field.get(bean), clazz);
                } catch (IllegalAccessException ex) {
                    throw new AccessToBeanFieldException(ex);
                }
            }
        }
        return val;
    }

    private static Boolean checkFilter(String fieldName, String excludeFields) {
        String[] fields2exclude = Strings.split(excludeFields, ";");
        for (String field2exclude : fields2exclude) {
            if (field2exclude.equalsIgnoreCase(fieldName))
                return false;
        }
        return true;
    }

    public static String buildBeanStateInfo(Object bean, String beanName, String tab, String excludeFields) {
        if (tab == null) tab = "";
        if (bean == null)
            return tab + (Strings.isNullOrEmpty(beanName) ? "null" : beanName + " null");
        final String attrFmt = tab + " - %s : %s;\n";
        StringBuilder out = new StringBuilder();
        Class<?> type = bean.getClass();
        String bnName = Strings.isNullOrEmpty(beanName) ? type.getName() : beanName;
        out.append(String.format(tab + "%s {\n", bnName));
        for (java.lang.reflect.Field fld : getAllObjectFields(type)) {
            if (checkFilter(fld.getName(), excludeFields))
                out.append(String.format(attrFmt, fld.getName(), fieldValueAsString(bean, fld)));
        }
        out.append(tab + "}");
        return out.toString();
    }

    public static String buildBeanStateInfo(Object bean, String beanName, String tab) {
        return buildBeanStateInfo(bean, beanName, tab, null);
    }

    public static String dictionaryInfo(Dictionary dict, String beanName, String tab) {
        if (tab == null) tab = "";
        final String attrFmt = tab + " - %s : %s;\n";
        StringBuilder out = new StringBuilder();
        out.append(String.format(tab + "%s {\n", beanName));
        for (Enumeration e = dict.keys(); e.hasMoreElements(); ) {
            Object key = e.nextElement();
            Object val = dict.get(key);
            out.append(String.format(attrFmt, key, val));
        }
        out.append(tab + "}");
        return out.toString();
    }

    public static String propertyInfo(Hashtable hashtable, String beanName, String tab) {
        if (tab == null) tab = "";
        final String attrFmt = tab + " - %s : %s;\n";
        StringBuilder out = new StringBuilder();
        out.append(String.format(tab + "%s {\n", beanName));
        for (Enumeration e = hashtable.keys(); e.hasMoreElements(); ) {
            Object key = e.nextElement();
            Object val = hashtable.get(key);
            out.append(String.format(attrFmt, key, val));
        }
        out.append(tab + "}");
        return out.toString();
    }

    public static boolean applyValuesToBeanFromDict(Dictionary vals, Object bean) {
        boolean result = false;
        if (vals == null)
            throw new IllegalArgumentException("Argument \"vals\" cannot be null!");
        if (bean == null)
            throw new IllegalArgumentException("Argument \"bean\" cannot be null!");
        Class<?> type = bean.getClass();
        for (java.lang.reflect.Field fld : getAllObjectFields(type)) {
            String fldName = fld.getName();
            Prop p = findAnnotation(Prop.class, fld);
            if (p != null)
                fldName = p.name();
            Object valObj = vals.get(fldName);
            if (valObj != null) {
                try {
                    Object val = (fld.getType() == Object.class) ? valObj : Converter.toType(valObj, fld.getType());
                    fld.setAccessible(true);
                    fld.set(bean, val);
                    if (!result) result = true;
                } catch (Exception e) {
                    throw new ApplyValuesToBeanException(fldName, String.format("Can't set value %s to field. Msg: %s", valObj, e.getMessage()));
                }
            }
        }
        return result;
    }

    public static boolean applyValuesToBeanFromMap(Map vals, Object bean, String inclideAttrs, String excludeAttrs) {
        boolean result = false;
        if (vals == null)
            throw new IllegalArgumentException("Argument \"vals\" cannot be null!");
        if (bean == null)
            throw new IllegalArgumentException("Argument \"bean\" cannot be null!");
        Class<?> type = bean.getClass();
        for (java.lang.reflect.Field fld : getAllObjectFields(type)) {
            String fldName = fld.getName();
            Prop p = findAnnotation(Prop.class, fld);
            if (p != null)
                fldName = p.name();

            boolean skip = !vals.containsKey(fldName) ||
                    (!Strings.isNullOrEmpty(inclideAttrs) && !Strings.containsIgnoreCase(inclideAttrs, ",", fldName)) ||
                    (!Strings.isNullOrEmpty(excludeAttrs) && Strings.containsIgnoreCase(excludeAttrs, ",", fldName));
            if (skip) continue;

            Object valObj = vals.get(fldName);
            if (valObj != null) {
                try {
                    Object val = (fld.getType() == Object.class) ? valObj : Converter.toType(valObj, fld.getType());
                    fld.setAccessible(true);
                    fld.set(bean, val);
                    if (!result) result = true;
                } catch (Exception e) {
                    throw new ApplyValuesToBeanException(fldName, String.format("Can't set value %s to field. Msg: %s", valObj, e.getMessage()));
                }
            }
        }
        return result;
    }

    public static boolean applyValuesToBeanFromMap(Map vals, Object bean) {
        return applyValuesToBeanFromMap(vals, bean, null, null);
    }

    public static boolean applyValuesToBeanFromABean(ABean vals, Object bean) {
        return applyValuesToBeanFromMap(vals, bean);
    }

    public static void applyValuesToABeanFromABean(ABean srcBean, ABean dstBean, boolean addIfNotExists) {
        boolean result = false;
        if (srcBean == null)
            throw new IllegalArgumentException("Argument \"srcBean\" cannot be null!");
        if (dstBean == null)
            throw new IllegalArgumentException("Argument \"dstBean\" cannot be null!");
        for (String key : srcBean.keySet()) {
            if (dstBean.containsKey(key))
                dstBean.put(key, srcBean.get(key));
            else {
                if (addIfNotExists)
                    dstBean.put(key, srcBean.get(key));
            }
        }
    }

    public static void applyValuesToABeanFromMap(Map vals, ABean dstBean, boolean addIfNotExists) {
        boolean result = false;
        if (vals == null)
            throw new IllegalArgumentException("Argument \"srcBean\" cannot be null!");
        if (dstBean == null)
            throw new IllegalArgumentException("Argument \"dstBean\" cannot be null!");
        for (Object key : vals.keySet()) {
            if (dstBean.containsKey(key))
                dstBean.put(key.toString(), vals.get(key));
            else {
                if (addIfNotExists)
                    dstBean.put(key.toString(), vals.get(key));
            }
        }
    }

    private static Field findFieldOfBean(Class<?> type, String fieldName) {
        for (java.lang.reflect.Field fld : getAllObjectFields(type)) {
            Prop p = findAnnotation(Prop.class, fld);
            String annotatedFieldName = null;
            if (p != null)
                annotatedFieldName = p.name();
            if (Strings.compare(fld.getName(), fieldName, true) ||
                    (!Strings.isNullOrEmpty(annotatedFieldName) && Strings.compare(annotatedFieldName, fieldName, true)))
                return fld;
        }
        return null;
    }

    public static Object arrayCopyOf(Object original) {
//        T[] copy = ((Object)newType == (Object)Object[].class)
//                ? (T[]) new Object[newLength]
//                : (T[]) Array.newInstance(newType.getComponentType(), newLength);
//        System.arraycopy(original, 0, copy, 0,
//                Math.min(original.length, newLength));
//        return copy;
        if (original != null && original.getClass().isArray()) {
            int l = ((Object[]) original).length;
            Class<?> originalType = original.getClass();
            Object rslt = Array.newInstance(originalType.getComponentType(), l);
            System.arraycopy(original, 0, rslt, 0, l);
            return rslt;
        }
        return null;
    }

    public static boolean applyValuesToBeanFromBean(Object srcBean, Object bean) {
        boolean result = false;
        if (srcBean == null)
            throw new IllegalArgumentException("Argument \"srcBean\" cannot be null!");
        if (bean == null)
            throw new IllegalArgumentException("Argument \"bean\" cannot be null!");
        Class<?> srcType = srcBean.getClass();
        Class<?> type = bean.getClass();
        for (java.lang.reflect.Field fld : getAllObjectFields(type)) {
            String fldName = fld.getName();
            Field srcFld = findFieldOfBean(srcType, fldName);
            if (srcFld == null)
                continue;
            try {
                srcFld.setAccessible(true);
                Object valObj = srcFld.get(srcBean);
                if (valObj != null) {
                    Object val;
                    if (valObj.getClass().isArray()) {
                        val = arrayCopyOf(valObj);
                    } else {
                        val = (fld.getType().equals(Object.class) || fld.getType().equals(valObj.getClass())) ? valObj : Converter.toType(valObj, fld.getType());
                    }
                    fld.setAccessible(true);
                    fld.set(bean, val);
                    result = true;
                }
            } catch (Exception e) {
                String msg = String.format("Can't set value to field. Msg: %s", e.getMessage());
                throw new ApplyValuesToBeanException(fldName, msg, e);
            }
        }
        return result;
    }

    public static <T> boolean arrayContains(T[] array, T item) {
        for (T itm : array)
            if (itm == item)
                return true;
        return false;
    }

    public static <T> T nvl(T a, T b) {
        return (a == null) ? b : a;
    }

    public static <T> T newInstance(Class<T> type) {
        try {
            return type.newInstance();
        } catch (Exception e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public static Object cloneBean(Object bean) {
        if (bean != null && !bean.getClass().isPrimitive()) {
            Class<?> type = bean.getClass();
            Object newBean = newInstance(type);
            applyValuesToBeanFromBean(bean, newBean);
            return newBean;
        }
        return null;
    }

    public static <T> T cloneBean1(T bean, Class<T> clazz) {
        if (bean != null && !clazz.isPrimitive()) {
            T newBean = newInstance(clazz);
            applyValuesToBeanFromBean(bean, newBean);
            return newBean;
        }
        return null;
    }

    public static String preparePath(String path, char pathSeparator) {
        if (Strings.isNullOrEmpty(path))
            return null;
        String rslt = path;
        if (pathSeparator == (char) 0)
            pathSeparator = File.separatorChar;
        rslt = rslt.replace('\\', pathSeparator);
        rslt = rslt.replace('/', pathSeparator);
        return rslt;
    }

    public static String normalizePath(String path, char pathSeparator) {
        if (Strings.isNullOrEmpty(path))
            return null;
        if (pathSeparator == (char) 0)
            pathSeparator = File.separatorChar;
        String rslt = preparePath(path, pathSeparator);
        rslt = rslt.endsWith("" + pathSeparator) ? rslt : rslt + pathSeparator;
        return rslt;
    }

    public static String normalizePath(String path) {
        return normalizePath(path, (char) 0);
    }

    public static String generateTmpFileName(final String tmpPath, final String fileName) {
        String randomUUIDString = UUID.randomUUID().toString().replaceAll("-", "");
        return String.format("%s%s-$(%s).%s", Utl.normalizePath(tmpPath), Utl.fileNameWithoutExt(fileName), randomUUIDString, Utl.fileNameExt(fileName));
    }

    // хз пока не получилось...
//    public static Class<?> getTypeParams(Object obj) {
//        if(obj == null)
//            throw new IllegalArgumentException("Param obj cannot be null!!");
//        Class<?> clazz = obj.getClass();
//        TypeVariable<?>[] params = clazz.getTypeParameters();
//        if(params.length > 0) {
//            TypeVariable<?> paramFirst = params[0];
//            GenericDeclaration gd = paramFirst.getGenericDeclaration();
//            String pname = paramFirst.getName();
//            for(java.lang.reflect.Field fld : clazz.getDeclaredFields()) {
//                if(pname.compare("" + fld.getGenericType())) {
//                    fld.setAccessible(true);
//                    Object fldVal;
//                    try {
//                        fldVal=fld.get(obj);
//                    } catch(IllegalAccessException e) {
//                        fldVal=null;
//                    }
//                    return (fldVal != null) ? fldVal.getClass() : null;
//                }
//            }
//            return null;
//        }
//        return null;
//    }

//    public static BundleContext getBundleContext(ServletContext servletContext) {
//        return (BundleContext) servletContext.getAttribute("osgi-bundlecontext");
//    }

//    public static <T> T getService(BundleContext bundleContext, Class<T> serviceInterface) {
//        if (bundleContext == null)
//            throw new IllegalStateException("osgi-bundlecontext not registered!");
//
//        ServiceReference<T> serviceReference = bundleContext.getServiceReference(serviceInterface);
//        if (serviceReference != null)
//            return (T) bundleContext.getService(serviceReference);
//        else
//            throw new IllegalStateException(String.format("Service %s not registered!", serviceInterface.getName()));
//    }

//    public static <T> T getService(ServletContext servletContext, Class<T> serviceInterface) {
//        BundleContext bundleContext = getBundleContext(servletContext);
//        if (bundleContext == null)
//            throw new IllegalStateException("osgi-bundlecontext not registered!");
//
//        return getService(bundleContext, serviceInterface);
//    }

    public static String extractModuleKey(String bioCode) {
        String[] bioCodeParts = Strings.split(bioCode, "@");
        if (bioCodeParts.length == 2)
            return bioCodeParts[0];
        return null;
    }

    public static String extractBioPath(String bioCode, String pathSeparator) {
        String[] bioCodeParts = Strings.split(bioCode, "@");
        if (bioCodeParts.length == 2) {
            String path = pathSeparator + bioCodeParts[1].replace(".", pathSeparator);
            return path;
        } else if (bioCodeParts.length == 1) {
            return pathSeparator + bioCode.replace(".", pathSeparator);
        }
        return null;
    }

    public static final String DEFAULT_BIO_PATH_SEPARATOR = "/";

    public static String extractBioPath(String bioCode) {
        return extractBioPath(bioCode, DEFAULT_BIO_PATH_SEPARATOR);
    }

    public static String extractBioParentPath(String bioCode, String pathSeparator) {
        String[] bioCodeParts = Strings.split(bioCode, ".");
        if (bioCodeParts.length > 1) {
            bioCodeParts = Arrays.copyOf(bioCodeParts, bioCodeParts.length - 1);
            bioCode = Strings.combineArray(bioCodeParts, ".");
            return extractBioPath(bioCode, pathSeparator);
        }
        return pathSeparator;
    }

    public static String extractBioParentPath(String bioCode) {
        return extractBioParentPath(bioCode, DEFAULT_BIO_PATH_SEPARATOR);
    }

    public static Byte[] toObjects(byte[] bytesPrim) {
        Byte[] bytes = new Byte[bytesPrim.length];
        int i = 0;
        for (byte b : bytesPrim) bytes[i++] = b; //Autoboxing
        return bytes;

    }

    public static byte[] toPrimitives(Byte[] oBytes) {

        byte[] bytes = new byte[oBytes.length];
        for (int i = 0; i < oBytes.length; i++) {
            bytes[i] = oBytes[i];
        }
        return bytes;
    }

    public static Boolean confIsEmpty(Dictionary conf) {
        if (conf == null || conf.isEmpty())
            return true;
        int count = 0;
        String componentKey = "component";
        for (Enumeration e = conf.keys(); e.hasMoreElements(); ) {
            e.nextElement();
            count++;
        }
        return (count == 1 && conf.get(componentKey) != null);
    }


    public static String md5(String fileName) {
        String md5 = null;
        try {
            try (FileInputStream fis = new FileInputStream(new File(fileName))) {
                md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
            }
        } catch (IOException e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
        return md5;
    }

    public static int storeInputStream(InputStream inputStream, Path path) {
        try {
            int len = 0;
            Files.createDirectories(path.getParent());
            try (OutputStream out = new FileOutputStream(new File(path.toString()))) {
                int read = 0;
                final byte[] bytes = new byte[1024];
                while ((read = inputStream.read(bytes)) != -1) {
                    out.write(bytes, 0, read);
                    len += read;
                }
            }
            return len;
        } catch (IOException e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public static Path storeBlob(byte[] blob, Path path) {
        try {
            Files.createDirectories(path.getParent());
            try (OutputStream out = new FileOutputStream(new File(path.toString()))) {
                out.write(blob);
            }
            return path;
        } catch (IOException e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public static Path storeBlob(byte[] blob, String path) {
        return storeBlob(blob, Paths.get(path));
    }

    public static int storeInputStream(InputStream inputStream, String path) {
        return storeInputStream(inputStream, Paths.get(path));
    }


    public static void storeString(String text, String path, String encoding) {
        try {
            try (PrintStream out = new PrintStream(new FileOutputStream(path), true, encoding)) {
                out.print(text);
            }
        } catch (IOException e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public static void storeString(String text, String path) {
        storeString(text, path, "utf-8");
    }

    public static InputStream openFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                InputStream inputStream = new FileInputStream(file);
                return inputStream;
            } else
                throw new FileNotFoundException(String.format("File \"%s\" not found!", filePath));
        } catch (IOException e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public static InputStream openFile(Path filePath) {
        return openFile(filePath.toString());
    }

    public static boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }

    public static String readFile(String filePath, String encoding) {
        try {
            String rslt = null;
            Path p = Paths.get(filePath);
            if (Files.exists(p))
                try (InputStream is = Utl.openFile(filePath)) {
                    rslt = Utl.readStream(is, encoding);
                }
            else
                throw new FileNotFoundException(String.format("Файл %s не наден!", filePath));
            return rslt;
        } catch (IOException e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public static String readFile(String filePath) {
        return readFile(filePath, "utf-8");
    }

    public static List<String> readFileAsList(String filePath, String encoding) {
        try {
            List<String> rslt = new ArrayList<>();
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                Charset charset = Charset.forName(encoding);
                try (BufferedReader br = Files.newBufferedReader(path, charset)) {
                    String line = br.readLine();
                    while (line != null) {
                        rslt.add(line);
                        line = br.readLine();
                    }
                }
            }
            return rslt;
        } catch (IOException e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public static List<String> readFileAsList(String filePath) {
        return readFileAsList(filePath, StandardCharsets.UTF_8.displayName());
    }

    public static void storeListToFile(List<String> list, String filePath, String encoding) {
        try {
            Path path = Paths.get(filePath);
            Charset charset = Charset.forName(encoding);
            try (BufferedWriter bw = Files.newBufferedWriter(path, charset)) {
                for (String line : list)
                    bw.write(line + System.lineSeparator());
            }
        } catch (IOException e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public static void storeListToFile(List<String> list, String filePath) {
        storeListToFile(list, filePath, StandardCharsets.UTF_8.displayName());
    }

    public static String generateUUID() {
        UUID uuid = UUID.randomUUID();
        String hex = uuid.toString().replace("-", "").toLowerCase();
        return hex;
    }

    public static Object getFieldValue(java.lang.reflect.Field fld, Object bean) {
        try {
            return fld.get(bean);
        } catch (IllegalAccessException e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public static void setFieldValue(java.lang.reflect.Field fld, Object bean, Object value) {
        try {
            fld.set(bean, value);
        } catch (IllegalAccessException e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public static List<Param> beanToParams(Object bean) {
        List<Param> result = new ArrayList<>();
        if (bean == null)
            return result;
        Class<?> srcType = bean.getClass();
        for (java.lang.reflect.Field fld : getAllObjectFields(srcType)) {
            String paramName = fld.getName();
            if (paramName.equals("this$1")) continue;
            if (!paramName.toLowerCase().startsWith("p_"))
                paramName = "p_" + paramName.toLowerCase();
            fld.setAccessible(true);
            Object valObj = getFieldValue(fld, bean);
            Param.Direction direction = Param.Direction.UNDEFINED;
            Prop prp = fld.getAnnotation(Prop.class);
            MetaType metaType = MetaTypeConverter.read(fld.getType());
            if (prp != null) {
                if (!Strings.isNullOrEmpty(prp.name()))
                    paramName = prp.name().toLowerCase();
                if (prp.metaType() != MetaType.UNDEFINED)
                    metaType = prp.metaType();
                direction = prp.direction();
            }
            result.add(Param.builder()
                    .name(paramName)
                    .type(metaType)
                    .direction(direction)
                    .value(valObj)
                    .build());
        }
        return result;
    }

    public static List<Param> abeanToParams(ABean bean) {
        List<Param> result = new ArrayList<>();
        if (bean == null)
            return result;
        for (String key : bean.keySet()) {
            String paramName = key;
            if (paramName.equals("this$1")) continue;
            if (!paramName.toLowerCase().startsWith("p_"))
                paramName = "p_" + paramName.toLowerCase();
            Object valObj = bean.get(key);
            result.add(Param.builder().name(paramName).value(valObj).build());
        }
        return result;
    }

    public static List<Param> hashmapToParams(HashMap<String, Object> bean) {
        List<Param> result = new ArrayList<>();
        if (bean == null)
            return result;
        for (String key : bean.keySet()) {
            String paramName = key;
            if (paramName.equals("this$1")) continue;
            if (!paramName.toLowerCase().startsWith("p_"))
                paramName = "p_" + paramName.toLowerCase();
            Object valObj = bean.get(key);
            result.add(Param.builder().name(paramName).value(valObj).build());
        }
        return result;
    }

    public static List<Param> anjsonToParams(String anjson) {
        List<Param> result = new ArrayList<>();
        if (Strings.isNullOrEmpty(anjson))
            return result;

        ABean bioParamsContainer = Jecksons.getInstance().decodeABean(anjson);
        if (bioParamsContainer.containsKey("bioParams")) {
            List<HashMap<String, Object>> bioParamsArray = (List) bioParamsContainer.get("bioParams");
            for (HashMap<String, Object> prm : bioParamsArray) {
                String paramName = (String) prm.get("name");
                if (!paramName.toLowerCase().startsWith("p_"))
                    paramName = "p_" + paramName.toLowerCase();
                Object valObj = prm.get("value");
                result.add(Param.builder().name(paramName).value(valObj).build());
            }
        }
        return result;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static Document loadXmlDocument(InputStream inputStream) {
        try {
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            f.setValidating(false);
            DocumentBuilder builder = f.newDocumentBuilder();
            return builder.parse(inputStream);
        } catch (Exception e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public static Document loadXmlDocument(String fileName) {
        try {
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            f.setValidating(false);
            DocumentBuilder builder = f.newDocumentBuilder();
            InputStream inputStream = openFile(fileName);
            return builder.parse(inputStream);
        } catch (Exception e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public static void writeInputToOutput(InputStream input, OutputStream output) {
        try {
            BufferedInputStream buf = null;
            try {
                buf = new BufferedInputStream(input);
                int readBytes = 0;
                while ((readBytes = buf.read()) != -1)
                    output.write(readBytes);
            } finally {
                if (output != null)
                    output.flush();
                output.close();
                if (buf != null)
                    buf.close();
            }
        } catch (Exception e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }


    public static void writeFileToOutput(File file, OutputStream output) {
        try {
            writeInputToOutput(new FileInputStream(file), output);
        } catch (IOException e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public static void deleteFile(Path filePath, Boolean silent) {
        try {
            Files.delete(filePath);
        } catch (Exception e) {
            if (!silent)
                throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public static void deleteFile(String filePath, Boolean silent) {
        deleteFile(Paths.get(filePath), silent);
    }

    public static LoginRec parsLogin(String login) {
        LoginRec rslt = new LoginRec();
        String[] loginParts = Strings.split(login, "/");
        if (loginParts.length > 0)
            rslt.setUsername(loginParts[0]);
        if (loginParts.length > 1)
            rslt.setPassword(loginParts[1]);
        return rslt;
    }

    public static <T extends Enum<T>> T enumValueOf(
            Class<T> enumeration, String name, T defaultVal) {

        for (T enumValue : enumeration.getEnumConstants()) {
            if (enumValue.name().equalsIgnoreCase(name)) {
                return enumValue;
            }
        }

        if (defaultVal != null)
            return defaultVal;
        throw new IllegalArgumentException(String.format(
                "There is no value with name '%s' in Enum %s",
                name, enumeration.getName()
        ));
    }

    public static Filter restoreSimpleFilter(String simpleFilter) {
        try {
            if (simpleFilter.length() == 0 || !simpleFilter.startsWith("{") || !simpleFilter.endsWith("}"))
                throw new Exception(String.format("Error in structure of filter \"%s\"!", simpleFilter));
            simpleFilter = simpleFilter.substring(1, simpleFilter.length() - 1);
            Filter rslt = new Filter();
            String[] items = Strings.split(simpleFilter, ",");

            Expression rootAnd = and();
            for (String item : items) {
                String[] fildvalItems = Strings.split(item, ":");
                if (fildvalItems.length != 2)
                    throw new Exception(String.format("Error in structure of item \"%s\"!", item));
                String checkedFieldName = Regexs.find(fildvalItems[0], "\\w+", Pattern.CASE_INSENSITIVE);
                if (!fildvalItems[0].equals(checkedFieldName))
                    throw new Exception(String.format("Error in fieldName of item \"%s\"!", item));
                String[] fieldValues = Strings.split(fildvalItems[1], "\"|\"");
                if (fieldValues.length == 0 || !fildvalItems[1].startsWith("\"") || !fildvalItems[1].endsWith("\""))
                    throw new Exception(String.format("Error in fieldValue of item \"%s\"!", item));
                fieldValues[0] = fieldValues[0].substring(1);
                fieldValues[fieldValues.length - 1] = fieldValues[fieldValues.length - 1].substring(0, fieldValues[fieldValues.length - 1].length() - 1);
                if (fieldValues.length > 1) {
                    Expression itemOr = or();
                    for (String fieldValue : fieldValues)
                        itemOr.add(contains(checkedFieldName, fieldValue, true));
                    rootAnd.add(itemOr);
                } else
                    rootAnd.add(contains(checkedFieldName, fieldValues[0], true));
            }
            rslt.add(rootAnd);
            return rslt;
        } catch (Exception e) {
            if (LOG.isDebugEnabled())
                LOG.error("Error parsing simple filter \"{}\". Msg: {}", simpleFilter, e.getMessage());
            return null;
        }
    }

    public static List<Sort> restoreSimpleSort(String simpleSort) {
        List<Sort> rslt = new ArrayList<>();
        try {
            Sort sort;
            String[] items = Strings.split(simpleSort, ",", ";", "|");
            for (String item : items) {
                String checkedItem = Regexs.find(item, "(\\+|\\-)\\w+", Pattern.CASE_INSENSITIVE);
                if (item.equals(checkedItem)) {
                    sort = new Sort();
                    sort.setFieldName(item.substring(1));
                    sort.setDirection(item.substring(0, 1).equals("+") ? Sort.Direction.ASC : Sort.Direction.DESC);
                    rslt.add(sort);
                } else
                    return null;
            }
        } catch (Exception e) {
            if (LOG.isDebugEnabled())
                LOG.error("Error parsing simple sort \"{}\". Msg: {}", simpleSort, e.getMessage());
        }
        return rslt;
    }

    public static Properties loadProperties(InputStream inputStream) {
        try {
            Properties prop = new Properties();
            prop.load(inputStream);
            return prop;
        } catch (IOException e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public static Properties loadProperties(String filePath) {
        try {
            try (InputStream input = new FileInputStream(filePath)) {
                return loadProperties(input);
            }
        } catch (Exception e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public static long fileSize(String filePath) {
        try {
            long rslt = 0;
            Path p = Paths.get(filePath);
            if (Files.exists(p))
                rslt = p.toFile().length();
            else
                throw new FileNotFoundException(String.format("Файл %s не наден!", filePath));
            return rslt;
        } catch (IOException e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public static int generateIndxByProb(double[] prob) {
        double indxD = Math.random();
        int indxI = 0;
        double curLow = 0;
        for (int i = 0; i < prob.length; i++) {
            curLow += prob[i];
            if (indxD >= curLow - prob[i] && indxD < curLow) {
                indxI = i;
                break;
            }
        }
        return indxI;
    }

    public static RuntimeException wrapErrorAsRuntimeException(String msg, Exception e) {
        if (e != null && e instanceof RuntimeException)
            throw (RuntimeException) e;
        else {
            if (Strings.isNullOrEmpty(msg))
                return new RuntimeException(e);
            else if (e == null)
                return new RuntimeException(msg);
            else
                return new RuntimeException(msg, e);
        }
    }

    public static RuntimeException wrapErrorAsRuntimeException(Exception e) {
        return wrapErrorAsRuntimeException(null, e);
    }

    public static RuntimeException wrapErrorAsRuntimeException(String msg) {
        return wrapErrorAsRuntimeException(msg, null);
    }
}
