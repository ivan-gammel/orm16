<#-- @ftlvariable name="entity" converterType="com.esoftworks.orm16.processor.view.SerializedEntity" -->
<#-- @ftlvariable name="repository" converterType="com.esoftworks.orm16.processor.model.jdbc.RepositoryTemplate" -->
<#macro convert var attribute><#assign mapping=attribute.type().mapping() /><#if mapping.onWrite()??><#if mapping.onWrite().wrap()>${mapping.onWrite().converterType()}.${mapping.onWrite().method()}(</#if></#if>${var}<#if mapping.onWrite()??><#if mapping.onWrite().wrap()>)<#else>.${mapping.onWrite().method()}()</#if></#if></#macro>
<#macro query var attribute><@convert "${var}.${attribute.serializedName()}()" attribute /></#macro>

<#macro jdbcGet resultSetVar offsetVar offset attribute>${resultSetVar}.get${attribute.type().mapping().jdbcType()}(${offsetVar} + ${offset})</#macro>
<#macro parse resultSetVar offsetVar offset attribute><#assign mapping=attribute.type().mapping() /><#if mapping.cast()>(${attribute.type().name()}) </#if><#if mapping.onRead()??><#if mapping.onRead().wrap()>${mapping.onRead().converterType()}.${mapping.onRead().method()}(</#if></#if><@jdbcGet resultSetVar offsetVar offset attribute /><#if mapping.onRead()??><#if mapping.onRead().wrap()>)<#else>.${mapping.onRead().method()}()</#if></#if></#macro>


package ${repository.packageName()};

import java.sql.*;
import java.util.Optional;
<#list repository.imports() as import>
import ${import};
</#list>
import com.esoftworks.orm16.core.jdbc.*;
import ${entity.packageName()}.${entity.name()};

import static java.lang.String.valueOf;
import static java.lang.String.join;
import static com.esoftworks.orm16.core.repository.RepositoryExceptionFactory.*;

public record ${repository.name()}(JdbcDatabase database) <#list repository.interfaces()>
    implements <#items as interface>${interface}<#sep>,
    </#items></#list> {

    public ${repository.name()} {
        if (database == null) throw new NullPointerException("database");
    }

<#if entity.compositeKey()?? && entity.compositeKey().generated()>
    public static record ${entity.compositeKey().name()}(<#list entity.compositeKey().attributes() as attribute>${attribute.type().name()} ${attribute.name()}<#sep>, </#list>) {
        public ${entity.compositeKey().name()} {
<#list entity.compositeKey().attributes() as attribute>            if (${attribute.name()} == null) throw new NullPointerException("${attribute.name()}");
</#list>
        }
        public ${entity.compositeKey().name()} keyOf(${entity.name()} value) {
            return new ${entity.compositeKey().name()}(<#list entity.keys() as attribute>value.${attribute.name()}()<#sep>, </#list>);
        }
    }  
</#if>

    public static class ${entity.name()}Mapper {
        public static ${entity.name()} map(ResultSet rs) throws SQLException {
            return map(rs, 1);
        }

        public static ${entity.name()} map(ResultSet rs, int offset) throws SQLException {
            return new ${entity.name()}(
<#list entity.attributes() as attribute>                <@parse "rs" "offset" attribute_index attribute /><#sep>,
</#list>

            );
        }
    }

    <#if entity.compositeKey()??>
    public Optional<${entity.name()}> get(${entity.compositeKey().name()} key) {
        return get(<#list entity.compositeKey().attributes() as attribute>key.${attribute.name()}()<#sep>, </#list>);
    }
    </#if>

    public Optional<${entity.name()}> get(<#list entity.keys() as attribute>${attribute.type().name()} ${attribute.name()}<#sep>, </#list>) {
        try {
            return database.query(
                """
                    SELECT <#list entity.attributes() as attribute>${attribute.serializedName()}<#sep>, </#list>
                    FROM ${entity.serializedName()}
                    WHERE <#list entity.keys() as attribute>${attribute.serializedName()}=?<#sep> AND </#list>""",
                ${entity.name()}Mapper::map,
<#list entity.keys() as attribute>                <@convert attribute.name() attribute /><#sep>,
</#list>

            )
            .stream()
            .findFirst();
        } catch (SQLException e) {
            throw onQuery(e, <#list entity.keys() as attribute>${attribute.name()}<#sep>, </#list>);
        }
    }

    @Override
    public ${entity.name()} add(${entity.name()} value) {
        try {
            database.update(
                """
                    INSERT INTO ${entity.serializedName()} (<#list entity.attributes() as attribute>${attribute.serializedName()}<#sep>, </#list>)
                    VALUES (<#list entity.attributes() as attribute>?<#sep>, </#list>)""",
<#list entity.attributes() as attribute>                <@query "value" attribute /><#sep>,
</#list>

            );
            return value;
        } catch (SQLException e) {
            throw onCreate(e, <#list entity.keys() as attribute>value.${attribute.name()}()<#sep>, </#list>);
        }
    }

    @Override
    public ${entity.name()} update(${entity.name()} value) {
        try {
            database.update(
                """
                    UPDATE ${entity.serializedName()}
                    SET <#list entity.attributesExcludingKeys() as attribute>${attribute.serializedName()}=?<#sep>, </#list>
                    WHERE <#list entity.keys() as attribute>${attribute.serializedName()}=?<#sep> AND </#list>""",
<#list entity.attributesExcludingKeys() as attribute>                <@query "value" attribute />,
</#list>
<#list entity.keys() as attribute>                <@query "value" attribute /><#sep>,
</#list>

            );
            return value;
        } catch (SQLException e) {
            throw onUpdate(e, <#list entity.keys() as attribute>value.${attribute.name()}()<#sep>, </#list>);
        }
    }

    @Override
    public boolean remove(<#list entity.keys() as attribute>${attribute.type().name()} ${attribute.name()}<#sep>, </#list>) {
        try {
            int changes = database.update("DELETE FROM ${entity.serializedName()} WHERE <#list entity.keys() as attribute>${attribute.serializedName()}=?<#sep> AND </#list>", <#list entity.keys() as attribute>${attribute.name()}<#sep>, </#list>);
            return changes > 0;
        } catch (SQLException e) {
            throw onDelete(e, <#list entity.keys() as attribute>${attribute.name()}<#sep>, </#list>);
        }
    }
    
    <#if entity.compositeKey()??>
    public boolean remove(${entity.compositeKey().name()} key) {
            return remove(<#list entity.compositeKey().attributes() as attribute>key.${attribute.name()}()<#sep>, </#list>);
    }
    </#if>


}
