/*
 * EnvironmentPresenter.java
 *
 * Copyright (C) 2009-12 by RStudio, Inc.
 *
 * Unless you have received this program directly from RStudio pursuant
 * to the terms of a commercial license agreement with RStudio, then
 * this program is licensed to you under the terms of version 3 of the
 * GNU Affero General Public License. This program is distributed WITHOUT
 * ANY EXPRESS OR IMPLIED WARRANTY, INCLUDING THOSE OF NON-INFRINGEMENT,
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. Please refer to the
 * AGPL (http://www.gnu.org/licenses/agpl-3.0.txt) for more details.
 *
 */
package org.rstudio.studio.client.workbench.views.environment;

import org.rstudio.core.client.command.CommandBinder;
import org.rstudio.core.client.command.Handler;
import org.rstudio.studio.client.common.GlobalDisplay;
import org.rstudio.studio.client.server.ServerError;
import org.rstudio.studio.client.server.ServerRequestCallback;
import org.rstudio.studio.client.workbench.WorkbenchView;
import org.rstudio.studio.client.workbench.commands.Commands;
import org.rstudio.studio.client.workbench.views.BasePresenter;
import org.rstudio.studio.client.workbench.views.environment.model.EnvironmentServerOperations;
import org.rstudio.studio.client.workbench.views.environment.model.RObject;


import com.google.gwt.core.client.JsArray;
import com.google.inject.Inject;

public class EnvironmentPresenter extends BasePresenter
{
   public interface Binder extends CommandBinder<Commands, EnvironmentPresenter> {}
   
   public interface Display extends WorkbenchView
   {
      void addObject(RObject object);
      void clearObjects();
   }
   
   @Inject
   public EnvironmentPresenter(Display view,
                               EnvironmentServerOperations server,
                               Binder binder,
                               Commands commands,
                               GlobalDisplay globalDisplay)
   {
      super(view);
      binder.bind(commands, this);
      
      view_ = view;
      server_ = server;
      globalDisplay_ = globalDisplay;
      
      refreshView();
   }
   
   @Handler
   void onRefreshEnvironment()
   {
      refreshView();
   }
   
   private void refreshView()
   {
      view_.clearObjects();
      
      server_.listEnvironment(new ServerRequestCallback<JsArray<RObject>>() {

         @Override
         public void onResponseReceived(JsArray<RObject> objects)
         {
            for (int i = 0; i<objects.length(); i++)
               view_.addObject(objects.get(i));
         }
         
         @Override
         public void onError(ServerError error)
         {
            globalDisplay_.showErrorMessage("Error Listing Objects", 
                                            error.getUserMessage());
         }
      });
   }
   
   private final Display view_;
   private final EnvironmentServerOperations server_;
   private final GlobalDisplay globalDisplay_;
}
