package org.eclipse.recommenders.tests.completion.rcp.overrides

import java.util.List
import org.apache.commons.lang3.StringUtils
import org.eclipse.core.resources.ResourcesPlugin
import org.eclipse.recommenders.completion.rcp.IntelligentCompletionContextResolver
import org.eclipse.recommenders.utils.rcp.JavaElementResolver
import org.eclipse.recommenders.tests.jdt.JavaProjectFixture
import org.eclipse.recommenders.tests.jdt.TestJavaContentAssistContext
import org.junit.Test

import static junit.framework.Assert.*
import org.eclipse.recommenders.internal.completion.rcp.overrides.OverridesCompletionProposalComputer
 
class OverridesCompletionProposalComputerSmokeTest { 
  
 
	@Test
	def void test01(){
		val code = '''
		public class MyClass extends Object{
			$
			@Override$
			pu$blic$ bool$ean eq$uals(O$bject $o$){
				int $i = $o$.$ha$shCode$();
				retur$n$ $f$alse;
			$}$
		$}$
		'''
		
		exercise(code); 
	} 
	
	@Test
	def void test02(){
		val code = '''
		publ$ic cl$ass MyCla$ss{
			$
			@Override$
			pu$blic$ bool$ean eq$uals(O$bject $o$){
				int $i = $o$.$ha$shCode$();
				retur$n$ $f$alse;
			$}$
		$}$
		'''
		exercise(code); 
	} 
	
	def exercise(CharSequence code){
		val fixture = new JavaProjectFixture(ResourcesPlugin::workspace,"test")
		val struct = fixture.createFileAndParseWithMarkers(code.toString, "MyClass.java")
		val cu = struct.first;
		for(completionIndex : struct.second){
			val ctx = new TestJavaContentAssistContext(cu, completionIndex)
			val resolver = new JavaElementResolver()
			val recommender = MockRecommender::get
			val sut = new OverridesCompletionProposalComputer(recommender, new IntelligentCompletionContextResolver(resolver),resolver)
			sut.sessionStarted
			sut.computeCompletionProposals(ctx, null)
		}
	}
	
	def l(String spaceSeparatedElementNames){ 
		val elementNames = StringUtils::split(spaceSeparatedElementNames);
		return newArrayList(elementNames) as List<String>
	}
	def w (String [] chains){
		val List<List<String>> res = newArrayList();
		for(chain :chains){
			res.add(l(chain))
		}
		return res as List<List<String>>
	}
	
}