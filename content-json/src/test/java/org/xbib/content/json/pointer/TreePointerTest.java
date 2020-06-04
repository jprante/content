package org.xbib.content.json.pointer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.fasterxml.jackson.core.TreeNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public final class TreePointerTest {

    @Test
    public void attemptToBuildTokensFromNullRaisesAnError() {
        Assertions.assertThrows(NullPointerException.class, () ->
                TreePointer.tokensFromInput(null));
    }

    @Test
    public void buildingTokenListYellsIfIllegalPointer() {
        Assertions.assertThrows(JsonPointerException.class, () ->
            TreePointer.tokensFromInput("a/b"));
    }

    @Test
    public void buildingTokenListIsUnfazedByAnEmptyInput()
            throws JsonPointerException {
        assertEquals(TreePointer.tokensFromInput(""), new ArrayList<>());
    }

    @Test
    public void buildingTokenListIsUnfazedByEmptyToken()
            throws JsonPointerException {
        final List<ReferenceToken> expected
                = Collections.singletonList(ReferenceToken.fromCooked(""));
        final List<ReferenceToken> actual = TreePointer.tokensFromInput("/");
        assertEquals(actual, expected);
    }

    @Test
    public void tokenListRespectsOrder()
            throws JsonPointerException {
        final List<ReferenceToken> expected = Arrays.asList(
                ReferenceToken.fromRaw("/"),
                ReferenceToken.fromRaw("~"),
                ReferenceToken.fromRaw("x")
        );
        final List<ReferenceToken> actual
                = TreePointer.tokensFromInput("/~1/~0/x");

        assertEquals(actual, expected);
    }

    @Test
    public void tokenListAccountsForEmptyTokens()
            throws JsonPointerException {
        final List<ReferenceToken> expected = Arrays.asList(
                ReferenceToken.fromRaw("a"),
                ReferenceToken.fromRaw(""),
                ReferenceToken.fromRaw("b")
        );
        final List<ReferenceToken> actual
                = TreePointer.tokensFromInput("/a//b");

        assertEquals(actual, expected);
    }

    @Test
    public void gettingTraversalResultGoesNoFurtherThanFirstMissing() {
        @SuppressWarnings("unchecked")
        final TokenResolver<TreeNode> token1 = mock(TokenResolver.class);
        @SuppressWarnings("unchecked")
        final TokenResolver<TreeNode> token2 = mock(TokenResolver.class);
        final TreeNode missing = mock(TreeNode.class);

        when(token1.get(any(TreeNode.class))).thenReturn(null);

        final DummyPointer ptr = new DummyPointer(missing,
                Arrays.asList(token1, token2));

        final TreeNode node = mock(TreeNode.class);
        final TreeNode ret = ptr.get(node);
        verify(token1, only()).get(node);
        verify(token2, never()).get(any(TreeNode.class));

        assertNull(ret);
    }

    @Test
    public void gettingPathOfMissingNodeReturnsMissingNode() {
        @SuppressWarnings("unchecked")
        final TokenResolver<TreeNode> token1 = mock(TokenResolver.class);
        @SuppressWarnings("unchecked")
        final TokenResolver<TreeNode> token2 = mock(TokenResolver.class);
        final TreeNode missing = mock(TreeNode.class);

        when(token1.get(any(TreeNode.class))).thenReturn(null);

        final DummyPointer ptr = new DummyPointer(missing,
                Arrays.asList(token1, token2));

        final TreeNode node = mock(TreeNode.class);
        final TreeNode ret = ptr.path(node);
        verify(token1, only()).get(node);
        verify(token2, never()).get(any(TreeNode.class));

        assertSame(ret, missing);
    }

    @Test
    public void treePointerCanTellWhetherItIsEmpty() {
        final List<TokenResolver<TreeNode>> list = new ArrayList<>();

        assertTrue(new DummyPointer(null, list).isEmpty());

        @SuppressWarnings("unchecked")
        final TokenResolver<TreeNode> mock = mock(TokenResolver.class);

        list.add(mock);
        assertFalse(new DummyPointer(null, list).isEmpty());
    }

    @Test
    public void treeIsUnalteredWhenOriginalListIsAltered() {
        final List<TokenResolver<TreeNode>> list = new ArrayList<>();
        final DummyPointer dummy = new DummyPointer(null, list);

        @SuppressWarnings("unchecked")
        final TokenResolver<TreeNode> mock = mock(TokenResolver.class);
        list.add(mock);

        assertTrue(dummy.isEmpty());
    }

    private static final class DummyPointer
            extends TreePointer<TreeNode> {
        private DummyPointer(final TreeNode missing, final List<TokenResolver<TreeNode>> tokenResolvers) {
            super(missing, tokenResolvers);
        }
    }
}
