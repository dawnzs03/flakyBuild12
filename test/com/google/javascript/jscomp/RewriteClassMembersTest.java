/*
 * Copyright 2021 The Closure Compiler Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.javascript.jscomp;

import static com.google.javascript.jscomp.TranspilationUtil.CANNOT_CONVERT_YET;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Test cases for transpilation pass that replaces public class fields and class static blocks:
 * <code><pre>
 * class C {
 *   x = 2;
 *   ['y'] = 3;
 *   static a;
 *   static ['b'] = 'hi';
 *   static {
 *     let c = 4;
 *     this.z = c;
 *   }
 * }
 * </pre></code>
 */
@RunWith(JUnit4.class)
public final class RewriteClassMembersTest extends CompilerTestCase {

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    enableTypeInfoValidation();
    enableTypeCheck();
    replaceTypesWithColors();
    enableMultistageCompilation();
  }

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return (externs, root) -> {
      new Es6ExtractClasses(compiler).process(externs, root);
      new RewriteClassMembers(compiler).process(externs, root);
    };
  }

  private void computedFieldTest(Sources srcs, Expected originalExpected) {
    Expected modifiedExpected =
        expected(
            UnitTestUtils.updateGenericVarNamesInExpectedFiles(
                (FlatSources) srcs,
                originalExpected,
                ImmutableMap.of("COMPFIELD", "$jscomp$compfield$")));
    test(srcs, modifiedExpected);
  }

  @Test
  public void testCannotConvertYet() {
    testError(
        lines(
            "class C {", //
            "  static {",
            "    let x = 2",
            "    this.y = x",
            "  }",
            "}"),
        /*lines(
        "class C {}", //
        "{",
        "  let x = 2;",
        "  C.y = x", // TODO(b/235871861): Need to correct references to `this`
        "}")*/
        CANNOT_CONVERT_YET); // uses `this` in static block

    testError(
        lines(
            "class C extends B {", //
            "  static {",
            "    let x = super.y",
            "  }",
            "}"),
        CANNOT_CONVERT_YET); // uses `super`

    testError(
        lines(
            "class C {", //
            "  static {",
            "    C.x = 2",
            "    const y = this.x",
            "  }",
            "}"),
        /*lines(
        "class C {}", //
        "{",
        "  C.x = 2;",
        "  const y = C.x",
        "}")*/
        CANNOT_CONVERT_YET); // uses `this` in static block

    testError(
        lines(
            "let C = class {",
            "  static prop = 5;",
            "},",
            "D = class extends C {",
            "  static {",
            "    console.log(this.prop);",
            "  }",
            "}"),
        CANNOT_CONVERT_YET); // uses `this` in static block

    testError(
        lines(
            "var z = 1", //
            "class C {",
            "  static {",
            "    let x = 2",
            "    var z = 3;",
            "  }",
            "}"),
        CANNOT_CONVERT_YET); // `var` in static block
  }

  @Test
  public void testThisInNonStaticPublicField() {
    test(
        lines(
            "class A {", //
            "  b = 'word';",
            "  c = this.b;",
            "}"),
        lines(
            "class A {",
            "  constructor() {",
            "    this.b = 'word';",
            "    this.c = this.b;",
            "  }",
            "}"));

    test(
        lines(
            "let obj = { bar() { return 9; } };", //
            "class D {",
            "  e = obj;",
            "  f = this.e.bar() * 4;",
            "}"),
        lines(
            "let obj = { bar() { return 9; } };",
            "class D {",
            "  constructor() {",
            "    this.e = obj;",
            "    this.f = this.e.bar() * 4;",
            "  }",
            "}"));

    test(
        lines(
            "class Foo {", //
            "  y = 'apple';",
            "  x = () => { return this.y + ' and banana'; };",
            "}"),
        lines(
            "class Foo {",
            "  constructor() {",
            "    this.y = 'apple';",
            "    this.x = () => { return this.y + ' and banana'; };",
            "  }",
            "}"));

    test(
        lines(
            "class Bar {", //
            "  x = () => { this.method(); };",
            "  method() {}",
            "}"),
        lines(
            "class Bar {",
            "  constructor() {",
            "    this.x = () => { this.method(); };",
            "  }",
            "  method() {}",
            "}"));
  }

  @Test
  public void testSuperInNonStaticPublicField() {
    test(
        lines(
            "class Foo {",
            "  x() {",
            "    return 3;",
            "  }",
            "}",
            "class Bar extends Foo {",
            "  y = 1 + super.x();",
            "}"),
        lines(
            "class Foo {",
            "  x() {",
            "    return 3;",
            "  }",
            "}",
            "class Bar extends Foo {",
            "  constructor() {",
            "    super(...arguments);",
            "    this.y = 1 + super.x();",
            "  }",
            "}"));
  }

  @Test
  public void testThisInStaticField() {
    test(
        lines(
            "class C {", //
            "  static x = 2;",
            "  static y = () => this.x;",
            "}"),
        lines(
            "class C {}", //
            "C.x = 2;",
            "C.y = () => { return C.x; }"));

    test(
        lines(
            "class F {", //
            "  static a = 'there';",
            "  static b = this.c() + this.a;",
            "  static c() { return 'hi'; }",
            "}"),
        lines(
            "class F {", //
            "  static c() { return 'hi'; }",
            "}",
            "F.a = 'there';",
            "F.b = F.c() + F.a;"));
  }

  @Test
  public void testSuperInStaticField() {
    test(
        lines(
            "class Foo {",
            "  static x() {",
            "    return 5;",
            "  }",
            "  static y() {",
            "    return 20;",
            "  }",
            "}",
            "class Bar extends Foo {",
            "  static z = () => super.x() + 12 + super.y();",
            "}"),
        lines(
            "class Foo {",
            "  static x() {",
            "    return 5;",
            "  }",
            "  static y() {",
            "    return 20;",
            "  }",
            "}",
            "class Bar extends Foo {}",
            "Bar.z = () => { return Foo.x() + 12 + Foo.y(); }"));

    test(
        lines(
            "class Bar {",
            "  static a = { method1() {} };",
            "  static b = { method2() { super.method1(); } };",
            "}",
            "Object.setPrototypeOf = function(c, d) {}",
            "Object.setPrototypeOf(Foo.b, Foo.a);",
            "Foo.b.method2();"),
        lines(
            "class Bar {}",
            "Bar.a = { method1() {} };",
            "Bar.b = { method2() { super.method1(); } };",
            "Object.setPrototypeOf = function(c, d) {}",
            "Object.setPrototypeOf(Foo.b, Foo.a);",
            "Foo.b.method2();"));
  }

  @Test
  public void testComputedPropInNonStaticField() {
    test(
        lines(
            "/** @unrestricted */",
            "class C {", //
            "  ['x'];",
            "  ['y'] = 3;",
            "}"),
        lines(
            "class C {", //
            "  constructor() {",
            "    this['x'];",
            "    this['y'] = 3;",
            "  }",
            "}"));

    test(
        lines(
            "/** @unrestricted */",
            "class C {", //
            "  [1] = 1;",
            "  [2] = this[1];",
            "}"),
        lines(
            "class C {",
            "  constructor() {",
            "    this[1] = 1;",
            "    this[2] = this[1];",
            "  }",
            "}"));

    test(
        lines(
            "/** @unrestricted */",
            "let c = class C {", //
            "  static [1] = 2;",
            "  [2] = C[1]",
            "}"),
        lines(
            "const testcode$classdecl$var0 = class {",
            "  constructor() {",
            "    this[2] = testcode$classdecl$var0[1];",
            "  }",
            "};",
            "testcode$classdecl$var0[1] = 2;",
            "/** @constructor */ ",
            "let c = testcode$classdecl$var0;"));

    test(
        lines(
            "foo(/** @unrestricted */ class C {", //
            "  static [1] = 2;",
            "  [2] = C[1]",
            "})"),
        lines(
            "const testcode$classdecl$var0 = class {",
            "  constructor() {",
            "    this[2] = testcode$classdecl$var0[1];",
            "  }",
            "};",
            "testcode$classdecl$var0[1] = 2;",
            "foo(testcode$classdecl$var0);"));

    test(
        lines(
            "let c = class {", //
            "  x = 1",
            "  y = this.x",
            "}",
            "/** @unrestricted */",
            "class B {",
            "  [1] = 2;",
            "  [2] = this[1]",
            "}"),
        lines(
            "let c = class {",
            "  constructor() {",
            "    this.x = 1;",
            "    this.y = this.x;",
            "  }",
            "};",
            "class B {",
            "  constructor() {",
            "    this[1] = 2;",
            "    this[2] = this[1];",
            "  }",
            "}"));
  }

  @Test
  public void testComputedPropInStaticField() {
    test(
        lines(
            "/** @unrestricted */",
            "class C {", //
            "  static ['x'];",
            "  static ['y'] = 2;",
            "}"),
        lines(
            "class C {}", //
            "C['x'];",
            "C['y'] = 2;"));

    test(
        lines(
            "/** @unrestricted */",
            "class C {", //
            "  static [1] = 1;",
            "  static [2] = this[1];",
            "}"),
        lines(
            "class C {}", //
            "C[1] = 1;",
            "C[2] = C[1];"));

    test(
        lines(
            "/** @unrestricted */",
            "let c = class C {", //
            "  static [1] = 2;",
            "  static [2] = C[1]",
            "}"),
        lines(
            "const testcode$classdecl$var0 = class {};",
            "testcode$classdecl$var0[1] = 2;",
            "testcode$classdecl$var0[2] = testcode$classdecl$var0[1];",
            "/** @constructor */ ",
            "let c = testcode$classdecl$var0;"));

    test(
        lines(
            "foo(/** @unrestricted */ class C {", //
            "  static [1] = 2;",
            "  static [2] = C[1]",
            "})"),
        lines(
            "const testcode$classdecl$var0 = class {};",
            "testcode$classdecl$var0[1] = 2;",
            "testcode$classdecl$var0[2] = testcode$classdecl$var0[1];",
            "foo(testcode$classdecl$var0);"));

    test(
        lines(
            "foo(/** @unrestricted */ class {", //
            "  static [1] = 1",
            "})"),
        lines(
            "const testcode$classdecl$var0 = class {};",
            "testcode$classdecl$var0[1] = 1;",
            "foo(testcode$classdecl$var0);"));
  }

  @Test
  public void testSideEffectsInComputedField() {
    computedFieldTest(
        srcs(
            lines(
                "function bar() {",
                "  this.x = 3;", //
                "  /** @unrestricted */",
                "  class Foo {",
                "    y;",
                "    [this.x] = 2;",
                "  }",
                "}")),
        expected(
            lines(
                "function bar() {",
                "  this.x = 3;",
                "  var COMPFIELD$0;",
                "  class Foo {",
                "    constructor() {",
                "      this.y;",
                "      this[COMPFIELD$0] = 2;",
                "    }",
                "  }",
                "  COMPFIELD$0 = this.x;",
                "}")));

    computedFieldTest(
        srcs(
            lines(
                "class E {",
                "  y() { return 1; }",
                "}",
                "class F extends E {",
                "  x() {",
                "    return /** @unrestricted */ class {",
                "      [super.y()] = 4;",
                "    }",
                "  }",
                "}")),
        expected(
            lines(
                "class E {",
                "  y() { return 1; }",
                "}",
                "class F extends E {",
                "  x() {",
                "    var COMPFIELD$0;",
                "    const testcode$classdecl$var0 = class {",
                "      constructor() {",
                "        this[COMPFIELD$0] = 4;",
                "      }",
                "    };",
                "    COMPFIELD$0 = super.y();",
                "    return testcode$classdecl$var0;",
                "  }",
                "}")));

    computedFieldTest(
        srcs(
            lines(
                "function bar(num) {}",
                "/** @unrestricted */",
                "class Foo {",
                "  [bar(1)] = 'a';",
                "  static b = bar(3);",
                "  static [bar(2)] = bar(4);",
                "}")),
        expected(
            lines(
                "function bar(num) {}",
                "var COMPFIELD$0;",
                "var COMPFIELD$1;",
                "class Foo {",
                "  constructor() {",
                "    this[COMPFIELD$0] = 'a'",
                "  }",
                "}",
                "COMPFIELD$0 = bar(1);",
                "COMPFIELD$1 = bar(2);",
                "Foo.b = bar(3);",
                "Foo[COMPFIELD$1] = bar(4);")));

    computedFieldTest(
        srcs(
            lines(
                "let x = 'hello';",
                "/** @unrestricted */ class Foo {",
                "  static n = (x=5);",
                "  static [x] = 'world';",
                "}")),
        expected(
            lines(
                "let x = 'hello';",
                "var COMPFIELD$0;",
                "class Foo {}",
                "COMPFIELD$0 = x;",
                "Foo.n = x = 5;",
                "Foo[COMPFIELD$0] = 'world';")));

    computedFieldTest(
        srcs(
            lines(
                "function foo(num) {}",
                "/** @unrestricted */",
                "class Baz {",
                "  ['f' + foo(1)];",
                "  static x = foo(6);",
                "  ['m' + foo(2)]() {};",
                "  static [foo(3)] = foo(7);",
                "  [foo(4)] = 2;",
                "  get [foo(5)]() {}",
                "}")),
        expected(
            lines(
                "function foo(num) {}",
                "var COMPFIELD$0;",
                "var COMPFIELD$1;",
                "var COMPFIELD$2;",
                "class Baz {",
                "  constructor() {",
                "    this[COMPFIELD$0];",
                "    this[COMPFIELD$1] = 2;",
                "  }",
                "  [(COMPFIELD$0 = 'f' + foo(1), 'm' + foo(2))]() {}",
                "  get [(COMPFIELD$2 = foo(3), (COMPFIELD$1 = foo(4), foo(5)))]() {}",
                "}",
                "Baz.x = foo(6);",
                "Baz[COMPFIELD$2] = foo(7);")));
  }

  @Test
  public void testClassStaticBlocksNoFieldAssign() {
    test(
        lines(
            "class C {", //
            "  static {",
            "  }",
            "}"),
        lines(
            "class C {", //
            "}",
            "{}"));

    test(
        lines(
            "class C {", //
            "  static {",
            "    let x = 2",
            "    const y = x",
            "  }",
            "}"),
        lines(
            "class C {}", //
            "{",
            "  let x = 2;",
            "  const y = x",
            "}"));

    test(
        lines(
            "class C {", //
            "  static {",
            "    let x = 2",
            "    const y = x",
            "    let z;",
            "    if (x - y == 0) {z = 1} else {z = 2}",
            "    while (x - z > 10) {z++;}",
            "    for (;;) {break;}",
            "  }",
            "}"),
        lines(
            "class C {}", //
            "{",
            "  let x = 2;",
            "  const y = x",
            "  let z;",
            "  if (x - y == 0) {z = 1} else {z = 2}",
            "  while (x - z > 10) {z++;}",
            "  for (;;) {break;}",
            "}"));

    test(
        lines(
            "class C {", //
            "  static {",
            "    let x = 2",
            "  }",
            "  static {",
            "    const y = x",
            "  }",
            "}"),
        lines(
            "class C {}", //
            "{",
            "  let x = 2;",
            "}",
            "{",
            "  const y = x",
            "}"));

    test(
        lines(
            "class C {", //
            "  static {",
            "    let x = 2",
            "  }",
            "  static {",
            "    const y = x",
            "  }",
            "}",
            "class D {",
            "  static {",
            "    let z = 1",
            "  }",
            "}"),
        lines(
            "class C {}", //
            "{",
            "  let x = 2;",
            "}",
            "{",
            "  const y = x",
            "}",
            "class D {}",
            "{",
            "  let z = 1;",
            "}"));

    test(
        lines(
            "class C {", //
            "  static {",
            "    let x = function () {return 1;}",
            "    const y = () => {return 2;}",
            "    function a() {return 3;}",
            "    let z = (() => {return 4;})();",
            "  }",
            "}"),
        lines(
            "class C {}", //
            "{",
            "  let x = function () {return 1;}",
            "  const y = () => {return 2;}",
            "  function a() {return 3;}",
            "  let z = (() => {return 4;})();",
            "}"));

    test(
        lines(
            "class C {", //
            "  static {",
            "    C.x = 2",
            // "    const y = C.x", //TODO(b/235871861) blocked on typechecking, gets
            // JSC_INEXISTENT_PROPERTY
            "  }",
            "}"),
        lines(
            "class C {}", //
            "{",
            "  C.x = 2;",
            // "  const y = C.x",
            "}"));

    test(
        lines(
            "class Foo {",
            "  static {",
            "    let x = 5;",
            "    class Bar {",
            "      static {",
            "        let x = 'str';",
            "      }",
            "    }",
            "  }",
            "}"),
        lines(
            "class Foo {}", //
            "{",
            "  let x = 5;",
            "  class Bar {}",
            "  {let x$jscomp$1 = 'str';}",
            "}"));
  }

  @Test
  public void testStaticNoncomputed() {
    test(
        lines(
            "class C {", //
            "  static x = 2",
            "}"),
        lines("class C {}", "C.x = 2;"));

    test(
        lines(
            "class C {", //
            "  static x;",
            "}"),
        lines("class C {}", "C.x;"));

    test(
        lines(
            "class C {", //
            "  static x = 2",
            "  static y = 'hi'",
            "  static z;",
            "}"),
        lines("class C {}", "C.x = 2;", "C.y = 'hi'", "C.z;"));

    test(
        lines(
            "class C {", //
            "  static x = 2",
            "  static y = 3",
            "}",
            "class D {",
            "  static z = 1",
            "}"),
        lines(
            "class C {}", //
            "C.x = 2;",
            "C.y = 3",
            "class D {}",
            "D.z = 1;"));

    test(
        lines(
            "class C {", //
            "  static w = function () {return 1;};",
            "  static x = () => {return 2;};",
            "  static y = (function a() {return 3;})();",
            "  static z = (() => {return 4;})();",
            "}"),
        lines(
            "class C {}", //
            "C.w = function () {return 1;};",
            "C.x = () => {return 2;};",
            "C.y = (function a() {return 3;})();",
            "C.z = (() => {return 4;})();"));

    test(
        lines(
            "class C {", //
            "  static x = 2",
            "  static y = C.x",
            "}"),
        lines(
            "class C {}", //
            "C.x = 2;",
            "C.y = C.x"));

    test(
        lines(
            "class C {", //
            "  static x = 2",
            "  static {let y = C.x}",
            "}"),
        lines(
            "class C {}", //
            "C.x = 2;",
            "{let y = C.x}"));
  }

  @Test
  public void testInstanceNoncomputedWithNonemptyConstructor() {
    test(
        lines(
            "class C {", //
            "  x = 1;",
            "  constructor() {",
            "    this.y = 2;",
            "  }",
            "}"),
        lines(
            "class C {", //
            "  constructor() {",
            "    this.x = 1",
            "    this.y = 2;",
            "  }",
            "}"));

    test(
        lines(
            "class C {", //
            "  x;",
            "  constructor() {",
            "    this.y = 2;",
            "  }",
            "}"),
        lines(
            "class C {", //
            "  constructor() {",
            "    this.x;",
            "    this.y = 2;",
            "  }",
            "}"));

    test(
        lines(
            "class C {", //
            "  x = 1",
            "  y = 2",
            "  constructor() {",
            "    this.z = 3;",
            "  }",
            "}"),
        lines(
            "class C {", //
            "  constructor() {",
            "    this.x = 1;",
            "    this.y = 2;",
            "    this.z = 3;",
            "  }",
            "}"));

    test(
        lines(
            "class C {", //
            "  x = 1",
            "  y = 2",
            "  constructor() {",
            "    alert(3);",
            "    this.z = 4;",
            "  }",
            "}"),
        lines(
            "class C {", //
            "  constructor() {",
            "    this.x = 1;",
            "    this.y = 2;",
            "    alert(3);",
            "    this.z = 4;",
            "  }",
            "}"));

    test(
        lines(
            "class C {", //
            "  x = 1",
            "  constructor() {",
            "    alert(3);",
            "    this.z = 4;",
            "  }",
            "  y = 2",
            "}"),
        lines(
            "class C {", //
            "  constructor() {",
            "    this.x = 1;",
            "    this.y = 2;",
            "    alert(3);",
            "    this.z = 4;",
            "  }",
            "}"));

    test(
        lines(
            "class C {", //
            "  x = 1",
            "  constructor() {",
            "    alert(3);",
            "    this.z = 4;",
            "  }",
            "  y = 2",
            "}",
            "class D {",
            "  a = 5;",
            "  constructor() { this.b = 6;}",
            "}"),
        lines(
            "class C {", //
            "  constructor() {",
            "    this.x = 1;",
            "    this.y = 2;",
            "    alert(3);",
            "    this.z = 4;",
            "  }",
            "}",
            "class D {",
            "constructor() {",
            "  this.a = 5;",
            "  this.b = 6",
            "}",
            "}"));
  }

  @Test
  public void testInstanceNoncomputedWithNonemptyConstructorAndSuper() {
    test(
        lines(
            "class A { constructor() { alert(1); } }",
            "class C extends A {", //
            "  x = 1;",
            "  constructor() {",
            "    super()",
            "    this.y = 2;",
            "  }",
            "}"),
        lines(
            "class A { constructor() { alert(1); } }",
            "class C extends A {", //
            "  constructor() {",
            "    super()",
            "    this.x = 1",
            "    this.y = 2;",
            "  }",
            "}"));

    test(
        lines(
            "class A { constructor() { this.x = 1; } }",
            "class C extends A {", //
            "  y;",
            "  constructor() {",
            "    super()",
            "    alert(3);",
            "    this.z = 4;",
            "  }",
            "}"),
        lines(
            "class A { constructor() { this.x = 1; } }",
            "class C extends A {", //
            "  constructor() {",
            "    super()",
            "    this.y;",
            "    alert(3);",
            "    this.z = 4;",
            "  }",
            "}"));

    test(
        lines(
            "class A { constructor() { this.x = 1; } }",
            "class C extends A {", //
            "  y;",
            "  constructor() {",
            "    alert(3);",
            "    super()",
            "    this.z = 4;",
            "  }",
            "}"),
        lines(
            "class A { constructor() { this.x = 1; } }",
            "class C extends A {", //
            "  constructor() {",
            "    alert(3);",
            "    super()",
            "    this.y;",
            "    this.z = 4;",
            "  }",
            "}"));
  }

  @Test
  public void testNonComputedInstanceWithEmptyConstructor() {
    test(
        lines(
            "class C {", //
            "  x = 2;",
            "  constructor() {}",
            "}"),
        lines(
            "class C {", //
            "  constructor() {",
            "    this.x = 2;",
            "  }",
            "}"));

    test(
        lines(
            "class C {", //
            "  x;",
            "  constructor() {}",
            "}"),
        lines(
            "class C {", //
            "  constructor() {",
            "    this.x;",
            "  }",
            "}"));

    test(
        lines(
            "class C {", //
            "  x = 2",
            "  y = 'hi'",
            "  z;",
            "  constructor() {}",
            "}"),
        lines(
            "class C {", //
            "  constructor() {",
            "    this.x = 2",
            "    this.y = 'hi'",
            "    this.z;",
            "  }",
            "}"));

    test(
        lines(
            "class C {", //
            "  x = 1",
            "  constructor() {",
            "  }",
            "  y = 2",
            "}"),
        lines(
            "class C {", //
            "  constructor() {",
            "    this.x = 1;",
            "    this.y = 2;",
            "  }",
            "}"));

    test(
        lines(
            "class C {", //
            "  x = 1",
            "  constructor() {",
            "  }",
            "  y = 2",
            "}",
            "class D {",
            "  a = 5;",
            "  constructor() {}",
            "}"),
        lines(
            "class C {", //
            "  constructor() {",
            "    this.x = 1;",
            "    this.y = 2;",
            "  }",
            "}",
            "class D {",
            "constructor() {",
            "  this.a = 5;",
            "}",
            "}"));

    test(
        lines(
            "class C {", //
            "  w = function () {return 1;};",
            "  x = () => {return 2;};",
            "  y = (function a() {return 3;})();",
            "  z = (() => {return 4;})();",
            "  constructor() {}",
            "}"),
        lines(
            "class C {", //
            "  constructor() {",
            "    this.w = function () {return 1;};",
            "    this.x = () => {return 2;};",
            "    this.y = (function a() {return 3;})();",
            "    this.z = (() => {return 4;})();",
            "  }",
            "}"));

    test(
        lines(
            "class C {", //
            "  static x = 2",
            "  constructor() {}",
            "  y = C.x",
            "}"),
        lines(
            "class C {", //
            "  constructor() { this.y = C.x; }",
            "}",
            "C.x = 2;"));
  }

  @Test
  public void testInstanceNoncomputedNoConstructor() {
    test(
        lines(
            "class C {", //
            "  x = 2;",
            "}"),
        lines(
            "class C {", //
            "  constructor() {this.x=2;}",
            "}"));

    test(
        lines(
            "class C {", //
            "  x;",
            "}"),
        lines(
            "class C {", //
            "  constructor() {this.x;}",
            "}"));

    test(
        lines(
            "class C {", //
            "  x = 2",
            "  y = 'hi'",
            "  z;",
            "}"),
        lines(
            "class C {", //
            "  constructor() {this.x=2; this.y='hi'; this.z;}",
            "}"));
    test(
        lines(
            "class C {", //
            "  foo() {}",
            "  x = 1;",
            "}"),
        lines(
            "class C {", //
            "  constructor() {this.x = 1;}",
            "  foo() {}",
            "}"));

    test(
        lines(
            "class C {", //
            "  static x = 2",
            "  y = C.x",
            "}"),
        lines(
            "class C {constructor() {",
            "this.y = C.x",
            "}}", //
            "C.x = 2;"));

    test(
        lines(
            "class C {", //
            "  w = function () {return 1;};",
            "  x = () => {return 2;};",
            "  y = (function a() {return 3;})();",
            "  z = (() => {return 4;})();",
            "}"),
        lines(
            "class C {", //
            "  constructor() {",
            "    this.w = function () {return 1;};",
            "    this.x = () => {return 2;};",
            "    this.y = (function a() {return 3;})();",
            "    this.z = (() => {return 4;})();",
            "  }",
            "}"));
  }

  @Test
  public void testInstanceNonComputedNoConstructorWithSuperclass() {
    test(
        lines(
            "class B {}", //
            "class C extends B {x = 1;}"),
        lines(
            "class B {}",
            "class C extends B {",
            "  constructor() {",
            "    super(...arguments);",
            "    this.x = 1;",
            "  }",
            "}"));
    test(
        lines(
            "class B {constructor() {}; y = 2;}", //
            "class C extends B {x = 1;}"),
        lines(
            "class B {constructor() {this.y = 2}}",
            "class C extends B {",
            "  constructor() {",
            "    super(...arguments);",
            "    this.x = 1;",
            "  }",
            "}"));
    test(
        lines(
            "class B {constructor(a, b) {}; y = 2;}", //
            "class C extends B {x = 1;}"),
        lines(
            "class B {constructor(a, b) {this.y = 2}}",
            "class C extends B {",
            "  constructor() {",
            "    super(...arguments);",
            "    this.x = 1;",
            "  }",
            "}"));
  }

  @Test
  public void testClassExpressionsStaticBlocks() {
    test(
        lines(
            "let c = class C {", //
            "  static {",
            "    C.y = 2;",
            "    let x = C.y",
            "  }",
            "}"),
        lines(
            "const testcode$classdecl$var0 = class {};",
            "{",
            "  testcode$classdecl$var0.y = 2;",
            "  let x = testcode$classdecl$var0.y;",
            "}",
            "/** @constructor */ ",
            "let c = testcode$classdecl$var0;"));

    test(
        lines(
            "foo(class C {", //
            "  static {",
            "    C.y = 2;",
            "    let x = C.y",
            "  }",
            "})"),
        lines(
            "var JSCompiler_temp_const$jscomp$0 = foo;",
            "const testcode$classdecl$var0 = class {};",
            "{",
            "  testcode$classdecl$var0.y = 2;",
            "  let x = testcode$classdecl$var0.y;",
            "}",
            "JSCompiler_temp_const$jscomp$0(testcode$classdecl$var0);"));

    test(
        lines(
            "class A { static b; }",
            "foo(A.b.c = class C {", //
            "  static {",
            "    C.y = 2;",
            "    let x = C.y",
            "  }",
            "})"),
        lines(
            "class A {}",
            "A.b;",
            "var JSCompiler_temp_const$jscomp$1 = foo;",
            "var JSCompiler_temp_const$jscomp$0 = A.b;",
            "const testcode$classdecl$var0 = class {};",
            "{",
            "  testcode$classdecl$var0.y = 2;",
            "  let x = testcode$classdecl$var0.y;",
            "}",
            "JSCompiler_temp_const$jscomp$1(JSCompiler_temp_const$jscomp$0.c =",
            "testcode$classdecl$var0);"));
  }

  @Test
  public void testNonClassDeclarationsStaticBlocks() {
    test(
        lines(
            "let c = class {", //
            "  static {",
            "    let x = 1",
            "  }",
            "}"),
        lines(
            "let c = class {}", //
            "{",
            "  let x = 1",
            "}"));

    test(
        lines(
            "class A {}",
            "A.c = class {", //
            "  static {",
            "    let x = 1",
            "  }",
            "}"),
        lines(
            "class A {}", //
            "A.c = class {}",
            "{",
            "  let x = 1",
            "}"));

    test(
        lines(
            "class A {}",
            "A[1] = class {", //
            "  static {",
            "    let x = 1",
            "  }",
            "}"),
        lines(
            "class A {}",
            "var JSCompiler_temp_const$jscomp$0 = A;",
            "const testcode$classdecl$var0 = class {};",
            "{",
            "  let x = 1;",
            "}",
            "JSCompiler_temp_const$jscomp$0[1] = testcode$classdecl$var0;"));
  }

  @Test
  public void testNonClassDeclarationsStaticNoncomputedFields() {
    test(
        lines(
            "let c = class {", //
            "  static x = 1",
            "}"),
        lines("let c = class {}", "c.x = 1"));

    test(
        lines(
            "class A {}",
            "A.c = class {", //
            "  static x = 1",
            "}"),
        lines("class A {}", "A.c = class {}", "A.c.x = 1"));

    test(
        lines(
            "class A {}",
            "A[1] = class {", //
            "  static x = 1",
            "}"),
        lines(
            "class A {}",
            "const testcode$classdecl$var0 = class {};",
            "testcode$classdecl$var0.x = 1;",
            "A[1] = testcode$classdecl$var0;"));

    test(
        lines(
            "let c = class C {", //
            "  static y = 2;",
            "  static x = C.y",
            "}"),
        lines(
            "const testcode$classdecl$var0 = class {};",
            "testcode$classdecl$var0.y = 2;",
            "testcode$classdecl$var0.x = testcode$classdecl$var0.y;",
            "/** @constructor */ ",
            "let c = testcode$classdecl$var0;"));

    test(
        lines(
            "foo(class C {", //
            "  static y = 2;",
            "  static x = C.y",
            "})"),
        lines(
            "const testcode$classdecl$var0 = class {};",
            "testcode$classdecl$var0.y = 2;",
            "testcode$classdecl$var0.x = testcode$classdecl$var0.y;",
            "foo(testcode$classdecl$var0);"));

    test(
        lines(
            "foo(class C {", //
            "  static y = 2;",
            "  x = C.y",
            "})"),
        lines(
            "const testcode$classdecl$var0 = class {",
            "  constructor() {",
            "    this.x = testcode$classdecl$var0.y;",
            "  }",
            "};",
            "testcode$classdecl$var0.y = 2;",
            "foo(testcode$classdecl$var0);"));
  }

  @Test
  public void testNonClassDeclarationsInstanceNoncomputedFields() {
    test(
        lines(
            "let c = class {", //
            "  y = 2;",
            "}"),
        lines(
            "let c = class {", //
            "  constructor() {",
            "    this.y = 2;",
            "  }",
            "}"));

    test(
        lines(
            "let c = class C {", //
            "  y = 2;",
            "}"),
        lines(
            "const testcode$classdecl$var0 = class {",
            "  constructor() {",
            "    this.y = 2;",
            "  }",
            "};",
            "/** @constructor */ ",
            "let c = testcode$classdecl$var0;"));

    test(
        lines(
            "class A {}",
            "A.c = class {", //
            "  y = 2;",
            "}"),
        lines(
            "class A {}",
            "A.c = class {", //
            "  constructor() {",
            "    this.y = 2;",
            "  }",
            "}"));

    test(
        lines(
            "A[1] = class {", //
            "  y = 2;",
            "}"),
        lines(
            "const testcode$classdecl$var0 = class {",
            "  constructor() {",
            "    this.y = 2;",
            "  }",
            "};",
            "A[1] = testcode$classdecl$var0;"));

    test(
        lines(
            "let c = class C {", //
            "  y = 2;",
            "}"),
        lines(
            "const testcode$classdecl$var0 = class {",
            "  constructor() {",
            "    this.y = 2;",
            "  }",
            "};",
            "/** @constructor */ ",
            "let c = testcode$classdecl$var0;"));

    test(
        lines(
            "class A {}",
            "A.c = class C {", //
            "  y = 2;",
            "}"),
        lines(
            "class A {}",
            "const testcode$classdecl$var0 = class {",
            "  constructor() {",
            "    this.y = 2;",
            "  }",
            "};",
            "/** @constructor */ ",
            "A.c = testcode$classdecl$var0;"));

    test(
        lines(
            "A[1] = class C {", //
            "  y = 2;",
            "}"),
        lines(
            "const testcode$classdecl$var0 = class {",
            "  constructor() {",
            "    this.y = 2;",
            "  }",
            "};",
            "A[1] = testcode$classdecl$var0;"));

    test(
        lines(
            "foo(class C {", //
            "  y = 2;",
            "})"),
        lines(
            "const testcode$classdecl$var0 = class {",
            "  constructor() {",
            "    this.y = 2;",
            "  }",
            "};",
            "foo(testcode$classdecl$var0);"));
  }

  @Test
  public void testConstuctorAndStaticFieldDontConflict() {
    test(
        lines(
            "let x = 2;", //
            "class C {",
            "  static y = x",
            "  constructor(x) {}",
            "}"),
        lines(
            "let x = 2;", //
            "class C {",
            "  constructor(x$jscomp$1) {}",
            "}",
            "C.y = x"));
  }

  @Test
  public void testInstanceInitializerShadowsConstructorDeclaration() {
    test(
        lines(
            "let x = 2;", //
            "class C {",
            "  y = x",
            "  constructor(x) {}",
            "}"),
        lines(
            "let x = 2;", //
            "class C {",
            "  constructor(x$jscomp$1) {",
            "    this.y = x;",
            "  }",
            "}"));

    test(
        lines(
            "let x = 2;", //
            "class C {",
            "  y = x;",
            "  constructor() { let x; }",
            "}"),
        lines(
            "let x = 2;",
            "class C {",
            "  constructor() {",
            "    this.y = x;",
            "    let x$jscomp$1;",
            "  }",
            "}"));

    test(
        lines(
            "let x = 2;", //
            "class C {",
            "  y = x",
            "  constructor() { {var x;} }",
            "}"),
        lines(
            "let x = 2;",
            "class C {",
            "  constructor() {",
            "    this.y = x;",
            "    {",
            "     var x$jscomp$1;",
            "    }",
            "  }",
            "}"));

    test(
        lines(
            "function f() { return 4; }", //
            "class C {",
            "  y = f();",
            "  constructor() {function f() { return 'str'; }}",
            "}"),
        lines(
            "function f() {",
            "  return 4;",
            "}",
            "class C {",
            "  constructor() {",
            "    this.y = f();",
            "    function f$jscomp$1() {",
            "      return 'str';",
            "    }",
            "  }",
            "}"));

    test(
        lines(
            "class Foo {", //
            "  constructor(x) {}",
            "  y = (x) => x;",
            "}"),
        lines(
            "class Foo {",
            "  constructor(x) {",
            "    this.y = x$jscomp$1 => {",
            "      return x$jscomp$1;",
            "    };",
            "  }",
            "}"));

    test(
        lines(
            "let x = 2;", //
            "class C {",
            "  y = (x) => x;",
            "  constructor(x) {}",
            "}"),
        lines(
            "let x = 2;",
            "class C {",
            "  constructor(x$jscomp$2) {",
            "    this.y = x$jscomp$1 => {",
            "      return x$jscomp$1;",
            "    };",
            "  }",
            "}"));
  }

  @Test
  public void testInstanceInitializerDoesntShadowConstructorDeclaration() {
    test(
        lines(
            "let x = 2;", //
            "class C {",
            "  y = x;",
            "  constructor() { {let x;} }",
            "}"),
        lines(
            "let x = 2;",
            "class C {",
            "  constructor() {",
            "    this.y = x;",
            "    {let x$jscomp$1;}",
            "  }",
            "}"));

    test(
        lines(
            "let x = 2;", //
            "class C {",
            "  y = x",
            "  constructor() {() => { let x; };}",
            "}"),
        lines(
            "let x = 2;", //
            "class C {",
            "  constructor() {",
            "    this.y = x;",
            "    () => { let x$jscomp$1; };",
            "  }",
            "}"));

    test(
        lines(
            "let x = 2;", //
            "class C {",
            "  y = x",
            "  constructor() {(x) => 3;}",
            "}"),
        lines(
            "let x = 2;", //
            "class C {",
            "  constructor() {",
            "    this.y = x;",
            "    (x$jscomp$1) => { return 3; };",
            "  }",
            "}"));
  }

  @Test
  public void testInstanceFieldInitializersDontBleedOut() {
    test(
        lines(
            "class C {", //
            "  y = z",
            "  method() { x; }",
            "  constructor(x) {}",
            "}"),
        lines(
            "class C {", //
            "  method() { x; }",
            "  constructor(x) {",
            "    this.y = z;",
            "  }",
            "}"));
  }

  @Test
  public void testNestedClassesWithShadowingInstanceFields() {
    test(
        lines(
            "let x = 2;",
            "class C {",
            "  y = () => {",
            "    class Foo { z = x }",
            "  };",
            "  constructor(x) {}",
            "}"),
        lines(
            "let x = 2;",
            "class C {",
            "  constructor(x$jscomp$1) {",
            "    this.y = () => {",
            "      class Foo {",
            "        constructor() {",
            "          this.z = x;",
            "        }",
            "      }",
            "    };",
            "  }",
            "}"));
  }

  // Added when fixing transpilation of real-world code that passed a class expression to a
  // constructor call.
  @Test
  public void testPublicFieldsInClassExpressionInNew() {
    test(
        lines(
            "let foo = new (", //
            "    class Bar {",
            "      x;",
            "      static y;",
            "    }",
            ")();"),
        lines(
            "const testcode$classdecl$var0 = class {",
            "  constructor() {",
            "    this.x;",
            "  }",
            "};",
            "testcode$classdecl$var0.y;",
            "let foo = new testcode$classdecl$var0();"));
  }

  @Test
  public void testNonClassDeclarationsFunctionArgs() {
    test(
        "A[foo()] = class {static x;}",
        lines(
            "var JSCompiler_temp_const$jscomp$1 = A;",
            "var JSCompiler_temp_const$jscomp$0 = foo();",
            "const testcode$classdecl$var0 = class {};",
            "testcode$classdecl$var0.x;",
            "JSCompiler_temp_const$jscomp$1[JSCompiler_temp_const$jscomp$0] =",
            "    testcode$classdecl$var0;"));

    test(
        "foo(c = class {static x;})",
        lines(
            "const testcode$classdecl$var0 = class {};",
            "testcode$classdecl$var0.x;",
            "foo(c = testcode$classdecl$var0);"));

    test(
        "function foo(c = class {static x;}) {}",
        lines(
            "function foo(c = (() => {",
            "  const testcode$classdecl$var0 = class {};",
            "  testcode$classdecl$var0.x;",
            "  return testcode$classdecl$var0;",
            "})()) {}"));
  }

  @Test
  public void testAnonymousClassExpression() {
    test(
        lines(
            "function foo() {", //
            "  return class {",
            "    y;",
            "    static x;",
            "  }",
            "}"),
        lines(
            "function foo() {",
            "  const testcode$classdecl$var0 = class {",
            "    constructor() {",
            "      this.y;",
            "    }",
            "  };",
            "  testcode$classdecl$var0.x;",
            "  return testcode$classdecl$var0;",
            "}"));

    test(
        lines(
            "foo(class {", //
            "  y = 2;",
            "})"),
        lines(
            "const testcode$classdecl$var0 = class {",
            "  constructor() {",
            "    this.y = 2;",
            "  }",
            "};",
            "foo(testcode$classdecl$var0);"));

    test(
        lines(
            "foo(class {", //
            "  static x = 1;",
            "})"),
        lines(
            "const testcode$classdecl$var0 = class {};",
            "testcode$classdecl$var0.x = 1;",
            "foo(testcode$classdecl$var0);"));
  }
}
