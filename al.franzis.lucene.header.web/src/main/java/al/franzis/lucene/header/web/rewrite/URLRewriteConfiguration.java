/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package al.franzis.lucene.header.web.rewrite;

import javax.servlet.ServletContext;

import com.ocpsoft.rewrite.config.Configuration;
import com.ocpsoft.rewrite.config.ConfigurationBuilder;
import com.ocpsoft.rewrite.config.Direction;
import com.ocpsoft.rewrite.servlet.config.DispatchType;
import com.ocpsoft.rewrite.servlet.config.Forward;
import com.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import com.ocpsoft.rewrite.servlet.config.Path;
import com.ocpsoft.rewrite.servlet.config.rule.Join;
import com.ocpsoft.rewrite.servlet.config.rule.TrailingSlash;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class URLRewriteConfiguration extends HttpConfigurationProvider
{
   private static final String ENTITY_NAME = "[a-zA-Z$_0-9]+";

   @Override
   public Configuration getConfiguration(ServletContext context)
   {

      return ConfigurationBuilder
               .begin()

               // Application mappings
               .addRule(Join.path("/").to("/index.jsf"))

               .addRule(Join.path("/{domain}").where("domain").matches(ENTITY_NAME)
                        .to("/scaffold/{domain}/list.jsf").withInboundCorrection())

               .addRule(Join.path("/{domain}/{id}").where("domain").matches(ENTITY_NAME).where("id").matches("\\d+")
                        .to("/scaffold/{domain}/view.jsf").withInboundCorrection())

               .addRule(Join.path("/{domain}/create").where("domain").matches(ENTITY_NAME)
                        .to("/scaffold/{domain}/create.jsf")
                        .withInboundCorrection())

               // 404 and Error
               .addRule(Join.path("/404").to("/404.jsf"))
               .addRule(Join.path("/error").to("/500.jsf"));
   }

   @Override
   public int priority()
   {
      return 1;
   }

}
