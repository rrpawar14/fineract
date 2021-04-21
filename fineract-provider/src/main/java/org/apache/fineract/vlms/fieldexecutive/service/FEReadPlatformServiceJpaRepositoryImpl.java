/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.vlms.fieldexecutive.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.vlms.fieldexecutive.domain.DocumentsData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class FEReadPlatformServiceJpaRepositoryImpl implements FEReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;

    @Autowired
    public FEReadPlatformServiceJpaRepositoryImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static final class DocumentMapper implements RowMapper<DocumentsData> {

        public String schema() {
            return " d.documents_name as documents_name from m_documents_type d ";
        }

        @Override
        public DocumentsData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            // final Long id = rs.getLong("id");
            final String documents_name = rs.getString("documents_name");
            // final boolean status = rs.getBoolean("status");

            return DocumentsData.instance(documents_name);
        }
    }

    @Override
    @Cacheable(value = "documents", key = "T(org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('CD')")
    public Collection<DocumentsData> retrieveAllDocumentsType() {
        this.context.authenticatedUser();

        final DocumentMapper rm = new DocumentMapper();
        final String sql = "select " + rm.schema() + " where d.status=1 order by d.documents_name";

        return this.jdbcTemplate.query(sql, rm, new Object[] {});
    }

    /*
     * @Override public DocumentsData retrieveCode(final Long codeId) { try { this.context.authenticatedUser();
     *
     * final DocumentMapper rm = new DocumentMapper(); final String sql = "select " + rm.schema() + " where c.id = ?";
     *
     * return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { codeId }); } catch (final
     * EmptyResultDataAccessException e) { throw new CodeNotFoundException(codeId, e); } }
     *
     * @Override public DocumentsData retriveCode(final String codeName) { try { this.context.authenticatedUser();
     *
     * final DocumentMapper rm = new DocumentMapper(); final String sql = "select " + rm.schema() +
     * " where c.code_name = ?";
     *
     * return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { codeName }); } catch (final
     * EmptyResultDataAccessException e) { throw new CodeNotFoundException(codeName, e); } }
     */
}
