/**
 * Copyright (c) 2010, 2012 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - Initial API
 */
package org.eclipse.recommenders.completion.rcp.processable;

import static com.google.common.base.Optional.fromNullable;
import static org.eclipse.recommenders.completion.rcp.processable.ProposalTag.IS_VISIBLE;
import static org.eclipse.recommenders.completion.rcp.processable.Proposals.copyStyledString;
import static org.eclipse.recommenders.internal.completion.rcp.l10n.LogMessages.ERROR_EXCEPTION_DURING_CODE_COMPLETION;
import static org.eclipse.recommenders.utils.Checks.ensureIsNotNull;
import static org.eclipse.recommenders.utils.Logs.log;

import java.lang.reflect.Field;
import java.util.Map;

import org.eclipse.jdt.core.CompletionProposal;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.text.java.AnonymousTypeCompletionProposal;
import org.eclipse.jdt.internal.ui.text.java.AnonymousTypeProposalInfo;
import org.eclipse.jdt.internal.ui.text.java.ProposalInfo;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.recommenders.utils.Reflections;
import org.eclipse.swt.graphics.Image;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

@SuppressWarnings({ "restriction", "unchecked" })
public class ProcessableAnonymousTypeCompletionProposal extends AnonymousTypeCompletionProposal
        implements IProcessableProposal {

    private static final Field F_SUPER_TYPE = Reflections
            .getDeclaredField(AnonymousTypeCompletionProposal.class, "fSuperType").orNull(); //$NON-NLS-1$

    private final Map<IProposalTag, Object> tags = Maps.newHashMap();
    private final CompletionProposal coreProposal;

    private ProposalProcessorManager mgr;
    private String lastPrefix;
    private String lastPrefixStyled;
    private StyledString initialDisplayString;
    private Image decoratedImage;

    public ProcessableAnonymousTypeCompletionProposal(CompletionProposal coreProposal,
            AnonymousTypeCompletionProposal uiProposal, JavaContentAssistInvocationContext context)
                    throws JavaModelException {
        super(context.getProject(), context.getCompilationUnit(), context, coreProposal.getReplaceStart(),
                uiProposal.getReplacementLength(), String.valueOf(coreProposal.getCompletion()),
                uiProposal.getStyledDisplayString(), String.valueOf(coreProposal.getDeclarationSignature()),
                findSupertype(uiProposal, coreProposal, context), uiProposal.getRelevance());
        this.coreProposal = coreProposal;
    }

    private static IType findSupertype(AnonymousTypeCompletionProposal uiProposal, CompletionProposal coreProposal,
            JavaContentAssistInvocationContext context) throws JavaModelException {
        if (F_SUPER_TYPE != null) {
            try {
                IType superType = (IType) F_SUPER_TYPE.get(uiProposal);
                if (superType != null) {
                    return superType;
                }
            } catch (Exception e) {
                log(ERROR_EXCEPTION_DURING_CODE_COMPLETION, e);
            }
        }
        return (IType) context.getProject().findElement(String.valueOf(coreProposal.getDeclarationKey()), null);
    }

    @Override
    protected ProposalInfo getProposalInfo() {
        ProposalInfo info = super.getProposalInfo();
        if (info == null) {
            final IJavaProject project = fInvocationContext.getProject();
            info = new AnonymousTypeProposalInfo(project, coreProposal);
            setProposalInfo(info);
        }
        return info;
    }

    @Override
    public Image getImage() {
        if (decoratedImage == null) {
            decoratedImage = mgr.decorateImage(super.getImage());
        }
        return decoratedImage;
    }

    @Override
    public StyledString getStyledDisplayString() {
        if (initialDisplayString == null) {
            initialDisplayString = super.getStyledDisplayString();
            StyledString copy = copyStyledString(initialDisplayString);
            StyledString decorated = mgr.decorateStyledDisplayString(copy);
            setStyledDisplayString(decorated);
        }
        if (lastPrefixStyled != lastPrefix) {
            lastPrefixStyled = lastPrefix;
            StyledString copy = copyStyledString(initialDisplayString);
            StyledString decorated = mgr.decorateStyledDisplayString(copy);
            setStyledDisplayString(decorated);
        }
        return super.getStyledDisplayString();
    }

    @Override
    public boolean isPrefix(final String prefix, final String completion) {
        lastPrefix = prefix;
        boolean res = mgr.prefixChanged(prefix) || super.isPrefix(prefix, completion);
        setTag(IS_VISIBLE, res);
        return res;
    }

    @Override
    public String getPrefix() {
        return lastPrefix;
    }

    @Override
    public Optional<CompletionProposal> getCoreProposal() {
        return fromNullable(coreProposal);
    }

    @Override
    public ProposalProcessorManager getProposalProcessorManager() {
        return mgr;
    }

    @Override
    public void setProposalProcessorManager(ProposalProcessorManager mgr) {
        this.mgr = mgr;
    }

    @Override
    public void setTag(IProposalTag key, Object value) {
        ensureIsNotNull(key);
        if (value == null) {
            tags.remove(key);
        } else {
            tags.put(key, value);
        }
    }

    @Override
    public <T> Optional<T> getTag(IProposalTag key) {
        return Optional.fromNullable((T) tags.get(key));
    }

    @Override
    public <T> Optional<T> getTag(String key) {
        return Proposals.getTag(this, key);
    }

    @Override
    public <T> T getTag(IProposalTag key, T defaultValue) {
        T res = (T) tags.get(key);
        return res != null ? res : defaultValue;
    }

    @Override
    public <T> T getTag(String key, T defaultValue) {
        return this.<T>getTag(key).or(defaultValue);
    }

    @Override
    public ImmutableSet<IProposalTag> tags() {
        return ImmutableSet.copyOf(tags.keySet());
    }
}
