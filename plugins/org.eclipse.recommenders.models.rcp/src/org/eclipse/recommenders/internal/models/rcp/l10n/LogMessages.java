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
package org.eclipse.recommenders.internal.models.rcp.l10n;

import static org.eclipse.core.runtime.IStatus.*;

import org.eclipse.recommenders.utils.Logs;
import org.eclipse.recommenders.utils.Logs.DefaultLogMessage;
import org.eclipse.recommenders.utils.Logs.ILogMessage;
import org.osgi.framework.Bundle;

public final class LogMessages extends DefaultLogMessage {

    private static int code = 1;

    private static final Bundle BUNDLE = Logs.getBundle(LogMessages.class);

    public static final ILogMessage ERROR_BIND_FILE_NAME = new LogMessages(ERROR, Messages.LOG_ERROR_BIND_FILE_NAME);
    public static final ILogMessage ERROR_CLOSING_MODEL_INDEX_SERVICE = new LogMessages(ERROR,
            Messages.LOG_ERROR_CLOSING_MODEL_INDEX_SERVICE);
    public static final ILogMessage ERROR_CREATE_EXECUTABLE_EXTENSION_FAILED = new LogMessages(ERROR,
            Messages.LOG_ERROR_CREATE_EXECUTABLE_EXTENSION_FAILED);
    public static final ILogMessage ERROR_FAILED_TO_ACCESS_MODEL_COORDINATES_CACHE = new LogMessages(ERROR,
            Messages.LOG_ERROR_FAILED_TO_ACCESS_MODEL_COORDINATES_CACHE);
    public static final ILogMessage ERROR_FAILED_TO_GET_CLASSPATH_ENTRY = new LogMessages(ERROR,
            Messages.LOG_ERROR_FAILED_TO_GET_CLASSPATH_ENTRY);
    public static final ILogMessage ERROR_FAILED_TO_LOAD_SECURE_PREFERENCE = new LogMessages(ERROR,
            Messages.LOG_ERROR_FAILED_TO_LOAD_SECURE_PREFERENCES);
    public static final ILogMessage ERROR_FAILED_TO_STORE_SECURE_PREFERENCE = new LogMessages(ERROR,
            Messages.LOG_ERROR_FAILED_TO_STORE_SECURE_PREFERENCES);
    public static final ILogMessage ERROR_FAILED_TO_OPEN_ECLIPSE_MODEL_REPOSITORY_FOLLOWING_MODEL_DELETION = new LogMessages(
            ERROR, Messages.LOG_ERROR_FAILED_TO_OPEN_ECLIPSE_MODEL_REPOSITORY_FOLLOWING_MODEL_DELETION);
    public static final ILogMessage ERROR_FAILED_TO_STORE_REMOTE_REPOSITORY_PREFERENCES = new LogMessages(ERROR,
            Messages.LOG_ERROR_FAILED_TO_STORE_REMOTE_REPOSITORY_PREFERENCES);
    public static final LogMessages ERROR_SAVE_PREFERENCES_FAILED = new LogMessages(ERROR,
            Messages.LOG_ERROR_SAVE_PREFERENCES_FAILED);

    public static final ILogMessage INFO_SERVICE_NOT_RUNNING = new LogMessages(INFO,
            Messages.LOG_ERROR_SERVICE_NOT_RUNNING);

    private LogMessages(int severity, String message)

    {
        super(severity, code++, message);
    }

    @Override
    public Bundle bundle() {
        return BUNDLE;
    }
}
