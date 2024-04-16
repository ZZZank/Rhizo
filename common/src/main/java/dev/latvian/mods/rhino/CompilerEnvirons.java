/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.latvian.mods.rhino;

import dev.latvian.mods.rhino.ast.ErrorCollector;

import java.util.Set;

public class CompilerEnvirons {

	public CompilerEnvirons() {
		this.errorReporter = DefaultErrorReporter.instance;
		this.reservedKeywordAsIdentifier = true;
		this.allowMemberExprAsFunctionName = false;
		this.generatingSource = true;
		this.strictMode = false;
		this.warningAsError = false;
		this.allowSharpComments = false;
		this.optimizationLevel = 0;
	}

	public void initFromContext(Context cx) {
		setErrorReporter(cx.getErrorReporter());
		reservedKeywordAsIdentifier = cx.hasFeature(Context.FEATURE_RESERVED_KEYWORD_AS_IDENTIFIER);
		allowMemberExprAsFunctionName = cx.hasFeature(Context.FEATURE_MEMBER_EXPR_AS_FUNCTION_NAME);
		strictMode = cx.hasFeature(Context.FEATURE_STRICT_MODE);
		warningAsError = cx.hasFeature(Context.FEATURE_WARNING_AS_ERROR);

		activationNames = cx.activationNames;

		optimizationLevel = cx.getOptimizationLevel();

		// Observer code generation in compiled code :
//		generateObserverCount = cx.generateObserverCount;
	}

	/**
	 * @deprecated
	 * Get the current language version.
	 * <p>
	 * The language version number affects JavaScript semantics as detailed
	 * in the overview documentation.
	 *
	 * @return an integer that is one of VERSION_1_0, VERSION_1_1, etc.
	 */
	public final int getLanguageVersion() {
		return Context.VERSION_ES6;
	}

	public final int getOptimizationLevel() {
		return optimizationLevel;
	}

	public void setOptimizationLevel(int level) {
		Context.checkOptimizationLevel(level);
		this.optimizationLevel = level;
	}

	public final ErrorReporter getErrorReporter() {
		return errorReporter;
	}

	public void setErrorReporter(ErrorReporter errorReporter) {
		if (errorReporter == null) {
			throw new IllegalArgumentException();
		}
		this.errorReporter = errorReporter;
	}

	public final boolean isReservedKeywordAsIdentifier() {
		return reservedKeywordAsIdentifier;
	}

	public void setReservedKeywordAsIdentifier(boolean flag) {
		reservedKeywordAsIdentifier = flag;
	}

	/**
	 * Extension to ECMA: if 'function &lt;name&gt;' is not followed
	 * by '(', assume &lt;name&gt; starts a {@code memberExpr}
	 */
	public final boolean isAllowMemberExprAsFunctionName() {
		return allowMemberExprAsFunctionName;
	}

	public void setAllowMemberExprAsFunctionName(boolean flag) {
		allowMemberExprAsFunctionName = flag;
	}

	public final boolean isGeneratingSource() {
		return generatingSource;
	}

	public boolean getWarnTrailingComma() {
		return warnTrailingComma;
	}

	public void setWarnTrailingComma(boolean warn) {
		warnTrailingComma = warn;
	}

	public final boolean isStrictMode() {
		return strictMode;
	}

	public void setStrictMode(boolean strict) {
		strictMode = strict;
	}

	public final boolean reportWarningAsError() {
		return warningAsError;
	}

	/**
	 * Specify whether or not source information should be generated.
	 * <p>
	 * Without source information, evaluating the "toString" method
	 * on JavaScript functions produces only "[native code]" for
	 * the body of the function.
	 * Note that code generated without source is not fully ECMA
	 * conformant.
	 */
	public void setGeneratingSource(boolean generatingSource) {
		this.generatingSource = generatingSource;
	}

	/**
	 * @return true iff code will be generated with callbacks to enable
	 * instruction thresholds
	 */
	public boolean isGenerateObserverCount() {
		return false;
	}

	/**
	 * @deprecated
	 * Turn on or off generation of code with callbacks to
	 * track the count of executed instructions.
	 * Currently only affects JVM byte code generation: this slows down the
	 * generated code, but code generated without the callbacks will not
	 * be counted toward instruction thresholds. Rhino's interpretive
	 * mode does instruction counting without inserting callbacks, so
	 * there is no requirement to compile code differently.
	 *
	 * @param generateObserverCount if true, generated code will contain
	 *                              calls to accumulate an estimate of the instructions executed.
	 */
	public void setGenerateObserverCount(boolean generateObserverCount) {
	}

	/**
	 * @deprecated
	 * @return false
	 */
	public boolean isRecordingComments() {
		return false;
	}

	/**
	 * @deprecated
	 */
	public void setRecordingComments(boolean record) {
	}

	/**
	 * @deprecated
	 * @return false
	 */
	public boolean isRecordingLocalJsDocComments() {
		return false;
	}

	/**
	 * @deprecated
	 */
	public void setRecordingLocalJsDocComments(boolean record) {
	}

	/**
	 * @deprecated
	 * Turn on or off full error recovery.  In this mode, parse errors do not
	 * throw an exception, and the parser attempts to build a full syntax tree
	 * from the input.  Useful for IDEs and other frontends.
	 */
	public void setRecoverFromErrors(boolean recover) {
	}

	/**
	 * @deprecated
	 * @return false
	 */
	public boolean recoverFromErrors() {
		return false;
	}

	/**
	 * @deprecated "IDE" mode is removed
	 * <p>
	 * Puts the parser in "IDE" mode.  This enables some slightly more expensive
	 * computations, such as figuring out helpful error bounds.
	 */
	public void setIdeMode(boolean ide) {
	}

	/**
	 * @deprecated
	 * @return false
	 */
	public boolean isIdeMode() {
		return false;
	}

	public Set<String> getActivationNames() {
		return activationNames;
	}

	public void setActivationNames(Set<String> activationNames) {
		this.activationNames = activationNames;
	}

	/**
	 * Mozilla sources use the C preprocessor.
	 */
	public void setAllowSharpComments(boolean allow) {
		allowSharpComments = allow;
	}

	public boolean getAllowSharpComments() {
		return allowSharpComments;
	}

	/**
	 * @deprecated "IDE" mode is removed
	 * <p>
	 * Returns a {@code CompilerEnvirons} suitable for using Rhino
	 * in an IDE environment.  Most features are enabled by default.
	 * The {@link ErrorReporter} is set to an {@link ErrorCollector}.
	 */
	public static CompilerEnvirons ideEnvirons() {
        return new CompilerEnvirons();
	}

	private ErrorReporter errorReporter;

	private int optimizationLevel;
	private boolean reservedKeywordAsIdentifier;
	private boolean allowMemberExprAsFunctionName;
	private boolean generatingSource;
	private boolean strictMode;
	private boolean warningAsError;
	private boolean warnTrailingComma;
	private boolean allowSharpComments;
	Set<String> activationNames;
}
