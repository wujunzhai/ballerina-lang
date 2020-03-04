package org.ballerinalang.langserver.codeaction.providers.openAPI.openAPIToBallerina;

import org.ballerinalang.annotation.JavaSPIService;
import org.ballerinalang.langserver.codeaction.providers.AbstractCodeActionProvider;
import org.ballerinalang.langserver.command.executors.openAPI.openAPIToBallerina.CreateOpenApiServiceResourceExecutor;
import org.ballerinalang.langserver.common.constants.CommandConstants;
import org.ballerinalang.langserver.common.utils.CommonUtil;
import org.ballerinalang.langserver.commons.LSContext;
import org.ballerinalang.langserver.commons.codeaction.CodeActionNodeType;
import org.ballerinalang.langserver.commons.command.CommandArgument;
import org.ballerinalang.langserver.commons.workspace.LSDocumentIdentifier;
import org.ballerinalang.langserver.commons.workspace.WorkspaceDocumentException;
import org.ballerinalang.langserver.commons.workspace.WorkspaceDocumentManager;
import org.ballerinalang.langserver.compiler.DocumentServiceKeys;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionKind;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Position;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;

import static org.ballerinalang.langserver.common.constants.CommandConstants.CREATE_SERVICE_RESOURCE;

/**
 * Code Action provider for open api service resource implement.
 *
 * @since 1.2.0
 */
@JavaSPIService("org.ballerinalang.langserver.commons.codeaction.spi.LSCodeActionProvider")
public class CreateOpenApiServiceResourceForPathCodeAction extends AbstractCodeActionProvider {
    private static final String RESOURCE_NOT_FOUND = "Couldn't find a Ballerina service resource for the path";

    @Override
    public List<CodeAction> getNodeBasedCodeActions(CodeActionNodeType nodeType, LSContext lsContext,
                                                    List<Diagnostic> allDiagnostics) {
        return null;
    }

    @Override
    public List<CodeAction> getDiagBasedCodeActions(CodeActionNodeType nodeType, LSContext lsContext,
                                                    List<Diagnostic> diagnosticsOfRange,
                                                    List<Diagnostic> allDiagnostics) {
        WorkspaceDocumentManager documentManager = lsContext.get(DocumentServiceKeys.DOC_MANAGER_KEY);
        Optional<Path> filePath = CommonUtil.getPathFromURI(lsContext.get(DocumentServiceKeys.FILE_URI_KEY));
        LSDocumentIdentifier document = null;
        try {
            document = documentManager.getLSDocument(filePath.get());
        } catch (WorkspaceDocumentException e) {
            // ignore
        }
        List<CodeAction> actions = new ArrayList<>();

        if (document == null) {
            return actions;
        }
        for (Diagnostic diagnostic : diagnosticsOfRange) {
            if (diagnostic.getMessage().startsWith(RESOURCE_NOT_FOUND)) {
                CodeAction codeAction = getOpenApiCommand(document, diagnostic, lsContext);
                if (codeAction != null) {
                    actions.add(codeAction);
                }
            }
        }
        return actions;
    }

    private static CodeAction getOpenApiCommand(LSDocumentIdentifier document, Diagnostic diagnostic,
                                                LSContext lsContext) {
        String diagnosticMessage = diagnostic.getMessage();
        Position position = diagnostic.getRange().getStart();
        int line = position.getLine();
        int column = position.getCharacter();
        String uri = lsContext.get(DocumentServiceKeys.FILE_URI_KEY);
        CommandArgument lineArg = new CommandArgument(CommandConstants.ARG_KEY_NODE_LINE, "" + line);
        CommandArgument colArg = new CommandArgument(CommandConstants.ARG_KEY_NODE_COLUMN, "" + column);
        CommandArgument uriArg = new CommandArgument(CommandConstants.ARG_KEY_DOC_URI, uri);
        List<Diagnostic> diagnostics = new ArrayList<>();

        Matcher matcher = CommandConstants.RESOURCE_PATH_NOT_FOUND.matcher(diagnosticMessage);
        if (matcher.find() && matcher.groupCount() > 0) {
            String path = matcher.group(1);
            String commandTitle = String.format(CommandConstants.CREATE_SERVICE_RESOURCE, path);
            CommandArgument pathArg = new CommandArgument(CommandConstants.ARG_KEY_PATH, path);

            List<Object> args = Arrays.asList(lineArg, colArg, uriArg, pathArg);
            CodeAction action = new CodeAction(commandTitle);
            action.setKind(CodeActionKind.QuickFix);
            action.setCommand(new Command(CREATE_SERVICE_RESOURCE, CreateOpenApiServiceResourceExecutor.COMMAND, args));
            action.setDiagnostics(diagnostics);
            return action;
        }
        return null;
    }

}
