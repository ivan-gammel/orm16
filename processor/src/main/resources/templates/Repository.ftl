<#-- @ftlvariable name="entity" converterType="com.esoftworks.orm16.processor.view.SerializedEntity" -->
<#-- @ftlvariable name="repository" converterType="com.esoftworks.orm16.processor.model.jdbc.RepositoryTemplate" -->


<#-- Prepared statement conversion -->
<#macro convert var attribute><#assign mapping=attribute.type().mapping() /><#if mapping.onWrite()??><#if mapping.onWrite().wrap()>${mapping.onWrite().converterType()}.${mapping.onWrite().method()}(</#if></#if>${var}<#if mapping.onWrite()??><#if mapping.onWrite().wrap()>)<#else>.${mapping.onWrite().method()}()</#if></#if></#macro>
<#macro query var attribute><@convert "${var}.${attribute.name()}()" attribute /></#macro>
<#macro queryAll var attributes>
<#list attributes as attribute><#if attribute.embedded()><@queryAll  var + "." + attribute.name() + "()" attribute.entity().attributes() /><#else>                <@query var attribute /></#if><#sep>,
</#list>
</#macro>

<#-- SQL query rendering -->
<#macro columns attributes><#list attributes as attribute><#if attribute.embedded()><@columns attribute.entity().attributes() /><#else>${attribute.serializedName()}</#if><#sep>, </#list></#macro>
<#macro equals attributes sep><#list attributes as attribute><#if attribute.embedded()><@equals attribute.entity().attributes() sep /><#else>${attribute.serializedName()}=?</#if><#sep>${sep}</#list></#macro>
<#macro inserts attributes><#list attributes as attribute><#if attribute.embedded()><@inserts attribute.entity().attributes() /><#else>?</#if><#sep>, </#list></#macro>

<#-- Result set conversion -->
<#macro jdbcGet resultSetVar offsetVar offset attribute>${resultSetVar}.get${attribute.type().mapping().jdbcType()}("${attribute.serializedName()}")</#macro>
<#macro parse resultSetVar offsetVar offset attribute><#assign mapping=attribute.type().mapping() /><#if mapping.cast()>(${attribute.type().name()}) </#if><#if mapping.onRead()??><#if mapping.onRead().wrap()>${mapping.onRead().converterType()}.${mapping.onRead().method()}(</#if></#if><@jdbcGet resultSetVar offsetVar offset attribute /><#if mapping.onRead()??><#if mapping.onRead().wrap()>)<#else>.${mapping.onRead().method()}()</#if></#if></#macro>
<#macro mapper entity>
    public static class ${entity.name()}Mapper {
        public static ${entity.name()} map(ResultSet rs) throws SQLException {
            return new ${entity.name()}(
<#list entity.attributes() as attribute>                <#if attribute.embedded()>${attribute.entity().name()}Mapper.map(rs)<#else><@parse "rs" "offset" attribute_index attribute /></#if><#sep>,
</#list>

            );
        }
    }
</#macro>

<#-- Main code -->
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
<#list entity.embeddedEntities() as embeddedEntity>
    <@mapper embeddedEntity />
</#list>
    <@mapper entity />

    <#if entity.compositeKey()??>
    public Optional<${entity.name()}> get(${entity.compositeKey().name()} key) {
        return get(<#list entity.compositeKey().attributes() as attribute>key.${attribute.name()}()<#sep>, </#list>);
    }
    </#if>

    public Optional<${entity.name()}> get(<#list entity.keys() as attribute>${attribute.type().name()} ${attribute.name()}<#sep>, </#list>) {
        try {
            return database.query(
                """
                    SELECT <@columns entity.attributes() />
                    FROM ${entity.serializedName()}
                    WHERE <@equals entity.keys() " AND " />""",
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
                    INSERT INTO ${entity.serializedName()} (<@columns entity.attributes() />)
                    VALUES (<@inserts entity.attributes() />)""",
<@queryAll "value" entity.attributes() />

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
                    SET <@equals entity.attributesExcludingKeys() ", " />
                    WHERE <@equals entity.keys() " AND " />""",
<@queryAll "value" entity.attributesExcludingKeys() />,
<@queryAll "value" entity.keys() />

            );
            return value;
        } catch (SQLException e) {
            throw onUpdate(e, <#list entity.keys() as attribute>value.${attribute.name()}()<#sep>, </#list>);
        }
    }

    @Override
    public boolean remove(<#list entity.keys() as attribute>${attribute.type().name()} ${attribute.name()}<#sep>, </#list>) {
        try {
            int changes = database.update("DELETE FROM ${entity.serializedName()} WHERE <@equals entity.keys() " AND " />", <#list entity.keys() as attribute>${attribute.name()}<#sep>, </#list>);
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
