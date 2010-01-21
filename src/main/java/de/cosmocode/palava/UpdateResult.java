/**
 * palava - a java-php-bridge
 * Copyright (C) 2007  CosmoCode GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package de.cosmocode.palava;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.InternetAddress;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.extension.JSONConstructor;
import org.json.extension.JSONEncoder;

/**
 * 
 *
 * @deprecated dont use this
 */
@Deprecated
public class UpdateResult implements Convertible, JSONEncoder {

    public static final String ERR_NULL = "missing";
    public static final String ERR_DIFFERENT = "different";
    public static final String ERR_FORMAT = "format";
    public static final String ERR_BADPASSWORD = "badpassword";
    public static final String ERR_NOTFOUND = "notfound";
    public static final String ERR_DUPLICATE = "duplicate";

    private static final Pattern PHONE = Pattern.compile("^[+ 0-9()/]*$");

    Map<String,List<String>> errors;
    Object result;
    Set<String> erroneousFields; // set of erroneous fields
    Exception exception;
    
    public boolean isError(){
        return errors != null || exception != null;
    }
    public Map<String,List<String>> getErrors() {
        return errors;
    }
    public Set<String> getFields() {
        return erroneousFields;
    }
    public boolean hasError( String field ) {
        return erroneousFields != null && erroneousFields.contains( field );
    }
    
    
    /** adds an error 
     * @param error a description of the error
     * @param fields the list of affected fields (i.e. some of the Result.args keys)
     */
    public void addError( String error, List<String> fields ) throws IllegalStateException {
        if ( error == null ) throw new NullPointerException("error");
        if ( fields == null ) throw new NullPointerException("fields");

        if ( errors == null ) errors = new HashMap<String,List<String>>();
        if ( erroneousFields == null ) erroneousFields = new HashSet<String>();
        
        List<String> _fields = errors.get(error);
        
        if ( _fields != null ) {
            _fields.addAll( fields );
        } else {
            errors.put(error, fields);
        }

        // fields

        erroneousFields.addAll( fields ) ;

    }
    public void setException( Exception e ) {
        this.exception = e;
    }
    /** adds an error 
     * @param error a description of the error
     * @param fields the list of affected fields (i.e. some of the Result.args keys)
     */
    public void addError( String error, String... fields   ) throws IllegalStateException {
        if ( error == null ) throw new NullPointerException("error");
        if ( fields == null ) throw new NullPointerException("fields");

        List<String> list = new ArrayList<String>();
        for ( String f : fields) list.add(f);
        
        addError( error, list );
    }
    
    @Override
    public void convert(StringBuffer sb, ContentConverter converter)
            throws ConversionException {
    
        if ( errors == null && result == null && exception == null ) {
            converter.convertKeyValue(sb, "status", "success", KeyValueState.SINGLE);            
        } else {
            converter.convertKeyValue(sb, "status", isError() ? "error" : "success", KeyValueState.START);            
            if ( errors != null )
                converter.convertKeyValue(sb, "errors", errors, KeyValueState.INSIDE);            
            if ( exception != null )
                converter.convertKeyValue(sb, "exception", exception.getMessage(), KeyValueState.INSIDE);            
            converter.convertKeyValue(sb, "result", result, KeyValueState.LAST);            
        }
    }
    public void encodeJSON (JSONConstructor json) throws JSONException {
        
        json.object();
        if ( errors == null && result == null && exception == null ) {
            json.key("status").value("success");
        } else {
            json.key("status").value( isError() ? "error" : "success" );
            if ( errors != null ) 
                json.key("errors").value(errors);
            if ( exception != null )
                json.key("exception").value(exception.toString());
            if ( result != null ) {
                json.key("result");
                if ( result instanceof JSONEncoder )
                    ((JSONEncoder)result).encodeJSON(json);
                else
                    json.value( result ) ;
            } 
        }

        json.endObject();
    }


    public Object getResult() {
        return result;
    }
    public void setResult(Object result) {
        this.result = result;
    }
    
    public boolean validateString( String field, String value ) {
        if ( value != null && value.length() > 0 ) return true;
        
        addError(ERR_NULL, field);
        
        return false;
    }
    
    public boolean validateEmail( String field, String value) {
        if ( ! validateString(field, value) ) return false;
        
        int idx;
        if ( (idx=value.indexOf('@')) <= 0 || value.indexOf('.',idx) == -1 ) {
            addError(ERR_FORMAT, field);
            return false;
        }
        
        try {
            new InternetAddress( value );
        } catch (Exception e) {
            addError(ERR_FORMAT, field);
            return false;
        }
        return true;
    }
    /** checks the validity of a phone number
     * adds a ERR_FORMAT on bad phone numbers
     * @param field
     * @param value
     * @param mandatory if true and value is null or empty: ERR_NULL is added
     * @return
     */
    public boolean validatePhone( String field, String value, boolean mandatory) {
        if ( mandatory ) {
            if( hasError(field) || ! validateString(field, value) ) return false;
        } else {
            if ( value == null || value.trim().length() == 0 ) return true;
        }
        Matcher m = PHONE.matcher(value);

        if(!m.matches()) {
            addError(UpdateResult.ERR_FORMAT, field);    
            return false;
        }
        return true;
    }
    public boolean validatePassword( String field, String password, int minLength ) {
        if ( password == null || password.length() < minLength ) {
            addError(UpdateResult.ERR_BADPASSWORD, field);
            return false;
        }
        return true;
    }
    /** lookup the field in the args and validate that the value is not different to compareTo
     * if the value is not equals, the ERR_DIFFERENT code is set on the field
     * example:
     *      String email = ur.lookupEmail(args,"email");
     *      if ( email != null ) validateEquals(args,"email2",email);
     *
     * example2:
     *      String password = ur.lookupPassword(args,"password",6);
     *      if ( password != null ) validateEquals(args,"password2",password);
     *
     * (note: we dont use the return value, because we check ur.isError anywehere else)
     * 
     *
     * @param args - the lookup map
     * @param field - the name of the field to lookup (eg "password2")
     * @param compareTo - the value to compare to
     *
     * @throws NullpointerException, if compareTo is null
     */
    public boolean validateEquals( Map<String, Object> args, String field, String compareTo ) {
        if (compareTo == null) throw new NullPointerException("compareTo");

        String value = lookupString(args,field);
        if ( value != null && ! value.equals(compareTo) ) {
            addError(ERR_DIFFERENT,field);
            return false;
        }
        return true;
    }
    public String lookupPhone(Map<String, Object> args, String field, boolean mandatory) {
        
        String value = lookupString( args, field, mandatory);
        
        return validatePhone(field, value,mandatory) ? value : null;
    }
    
    public String lookupEmail(Map<String, Object> args, String field) {
        return lookupEmail(args,field,true);
    }
    
    public String lookupEmail(Map<String, Object> args, String field,boolean mandatory) {
        String value = lookupString( args, field,mandatory);
        if (value==null) return null;
        if (value.length()==0) return null;
        else return validateEmail(field,value) ? value : null;
    }
    public String[] lookupEmails(Map<String, Object> args, String field) {
        String value = lookupString( args, field);
        if ( value == null ) return null;
        
        String[] values = value.split(",");
        for ( String v : values )
            if( !validateEmail(field, v) ) return null;

        return values;
    }
    public Long lookupLong(Map<String, Object> args, String field) {
        String value = (String) args.get(field) ;
        if ( value != null && ( value = value.trim()).length() == 0 ) return null;
        try {
            return Long.parseLong( value ) ;
        } catch ( NumberFormatException e ) {
            addError(field, ERR_FORMAT);
            return null;
        }
    }
    public Long lookupLong(Map<String, Object> args, String field, boolean mandatory) {
        Long value = lookupLong(args,field);
        if ( value == null && mandatory )
            addError(ERR_NULL,field);
        return value;
    }
    public String lookupString(Map<String, Object> args, String field) {
        return lookupString(args,field,true);
        
    }
    public String lookupString(Map<String, Object> args, String field, boolean mandatory) {
        String value = (String) args.get(field);
        if ( value != null ) value = value.trim();
        
        if ( mandatory ) return validateString(field, value ) ? value : null;
        else return value;
    }
    public Boolean lookupBoolean(Map<String, Object> args, String field) {
        String value = (String) args.get(field);
        return Boolean.parseBoolean(value);
    }
    public String lookupPassword(Map<String, Object> args, String field, int minLength) {
        String password = lookupString(args,field);
        if (hasError(field)) return password;
        return validatePassword(field,password,minLength) ? password : null;
    }
    
    public String lookupString(JSONObject json, String field) {
        try {
            return json.getString(field);
        } catch (JSONException e) {
            addError(ERR_NULL, field);
            return null;
        }
    }
    
    public Long lookupLong(JSONObject json, String field) {
        try {
            Long value = json.getLong(field);
            return value;
        } catch ( NullPointerException e ) {
            addError(ERR_NULL, field);
            return null;
        } catch ( JSONException e ) {
            addError(ERR_FORMAT, field);
            return null;
        }
    }
}
