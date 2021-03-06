/**
 * Copyright (c) 2014 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.internal.snipmatch.rcp.l10n;

import static org.eclipse.core.runtime.IStatus.*;

import org.eclipse.recommenders.utils.Logs;
import org.eclipse.recommenders.utils.Logs.DefaultLogMessage;
import org.eclipse.recommenders.utils.Logs.ILogMessage;
import org.osgi.framework.Bundle;

public final class LogMessages extends DefaultLogMessage {

    private static int code = 1;

    private static final Bundle BUNDLE = Logs.getBundle(LogMessages.class);

    public static final LogMessages ERROR_CREATING_SNIPPET_PROPOSAL_FAILED = new LogMessages(ERROR,
            Messages.LOG_ERROR_CREATING_SNIPPET_PROPOSAL_FAILED);
    public static final LogMessages ERROR_SNIPPET_COULD_NOT_BE_EVALUATED = new LogMessages(ERROR,
            Messages.LOG_ERROR_SNIPPET_COULD_NOT_BE_EVALUATED);
    public static final LogMessages ERROR_CANNOT_COMPUTE_LOCATION = new LogMessages(ERROR,
            Messages.LOG_ERROR_CANNOT_COMPUTE_LOCATION);
    public static final LogMessages ERROR_FAILED_TO_BIND_FILE = new LogMessages(ERROR,
            Messages.LOG_ERROR_FAILED_TO_BIND_FILE);
    public static final ILogMessage ERROR_FAILED_TO_EXECUTE_COMMAND = new LogMessages(ERROR,
            Messages.LOG_ERROR_FAILED_TO_EXECUTE_COMMAND);
    public static final LogMessages ERROR_FAILED_TO_DELETE_GIT_SNIPPET_REPOSITORY_ON_DISK = new LogMessages(ERROR,
            Messages.LOG_ERROR_FAILED_TO_DELETE_GIT_SNIPPET_REPOSITORY_ON_DISK);
    public static final LogMessages ERROR_FAILED_TO_JOIN_OPEN_JOB = new LogMessages(ERROR,
            Messages.LOG_ERROR_FAILED_TO_JOIN_OPEN_JOB);
    public static final LogMessages ERROR_FAILED_TO_LOAD_EDITOR_PAGE = new LogMessages(ERROR,
            Messages.LOG_ERROR_FAILED_TO_LOAD_EDITOR_PAGES);
    public static final LogMessages ERROR_FAILED_TO_OPEN_GIT_SNIPPET_REPOSITORY = new LogMessages(ERROR,
            Messages.LOG_ERROR_FAILED_TO_OPEN_GIT_SNIPPET_REPOSITORY);
    public static final LogMessages ERROR_FAILED_TO_OPEN_EDITOR = new LogMessages(ERROR,
            Messages.LOG_ERROR_FAILED_TO_OPEN_EDITOR);
    public static final LogMessages ERROR_FAILED_TO_OPEN_WIZARD_WITH_ZERO_HEIGHT_LIST = new LogMessages(ERROR,
            Messages.LOG_ERROR_FAILED_TO_OPEN_WIZARD_WITH_ZERO_HEIGHT_LIST);
    public static final LogMessages ERROR_FAILED_TO_READ_EXTENSION_POINT = new LogMessages(ERROR,
            Messages.LOG_ERROR_FAILED_TO_READ_EXTENSION_POINT);
    public static final LogMessages ERROR_FAILED_TO_RELOAD_REPOSITORIES = new LogMessages(ERROR,
            Messages.LOG_ERROR_FAILED_TO_RELOAD_REPOSITORIES);
    public static final LogMessages ERROR_FAILED_TO_STORE_SNIPPET = new LogMessages(ERROR,
            Messages.LOG_ERROR_FAILED_TO_STORE_SNIPPET);

    public static final ILogMessage ERROR_DEFAULT_REPO_CONFIGURATION_WITHOUT_ID = new LogMessages(ERROR,
            Messages.LOG_ERROR_DEFAULT_REPO_CONFIGURATION_WITHOUT_ID);
    public static final ILogMessage ERROR_LOADING_DEFAULT_REPO_CONFIGURATION = new LogMessages(ERROR,
            Messages.LOG_ERROR_LOADING_DEFAULT_REPO_CONFIGURATION);
    public static final ILogMessage ERROR_STORING_REPO_CONFIGURATION = new LogMessages(ERROR,
            Messages.LOG_ERROR_STORING_DEFAULT_REPO_CONFIGURATION);
    public static final ILogMessage ERROR_LOADING_REPO_CONFIGURATION = new LogMessages(ERROR,
            Messages.LOG_ERROR_LOADING_REPO_CONFIGURATION);
    public static final ILogMessage ERROR_STORING_DISABLED_REPOSITORY_CONFIGURATIONS = new LogMessages(ERROR,
            Messages.LOG_ERROR_STORING_DISABLED_REPOSITORY_CONFIGURATIONS);

    public static final LogMessages ERROR_LOADING_DEFAULT_GIT_REPO_CONFIGURATION = new LogMessages(ERROR,
            Messages.LOG_ERROR_LOADING_DEFAULT_GIT_REPO_CONFIGURATION);
    public static final LogMessages WARNING_DEFAULT_GIT_REPO_URL_DUPLICATE = new LogMessages(WARNING,
            Messages.LOG_WARNING_DEFAULT_GIT_REPO_URL_DUPLICATE);

    public static final ILogMessage ERROR_CREATING_INDEX_HEAD_DIFF = new LogMessages(ERROR,
            Messages.LOG_ERROR_CREATING_INDEX_HEAD_DIFF);
    public static final ILogMessage ERROR_SERVICE_NOT_RUNNING = new LogMessages(ERROR,
            Messages.LOG_ERROR_SERVICE_NOT_RUNNING);

    private LogMessages(int severity, String message) {
        super(severity, code++, message);
    }

    @Override
    public Bundle bundle() {
        return BUNDLE;
    }
}
