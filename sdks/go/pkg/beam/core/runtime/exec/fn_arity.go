// File generated by specialize. Do not edit.

// Licensed to the Apache Software Foundation (ASF) under one or more
// contributor license agreements.  See the NOTICE file distributed with
// this work for additional information regarding copyright ownership.
// The ASF licenses this file to You under the Apache License, Version 2.0
// (the "License"); you may not use this file except in compliance with
// the License.  You may obtain a copy of the License at
//
//	http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

// Code generated from fn_arity.tmpl. DO NOT EDIT.

package exec

import (
	"fmt"

	"github.com/apache/beam/sdks/v2/go/pkg/beam/core/typex"
	"github.com/apache/beam/sdks/v2/go/pkg/beam/core/util/reflectx"
)

// initCall initializes the caller for the invoker, avoiding slice allocation for the
// return values, and uses a cached return processor to handle the different possible
// return cases.
func (n *invoker) initCall() {
	switch fn := n.fn.Fn.(type) {

	case reflectx.Func0x0:
		n.call = func(pn typex.PaneInfo, ws []typex.Window, ts typex.EventTime) (*FullValue, error) {
			fn.Call0x0()
			return nil, nil
		}

	case reflectx.Func1x0:
		n.call = func(pn typex.PaneInfo, ws []typex.Window, ts typex.EventTime) (*FullValue, error) {
			fn.Call1x0(n.args[0])
			return nil, nil
		}

	case reflectx.Func2x0:
		n.call = func(pn typex.PaneInfo, ws []typex.Window, ts typex.EventTime) (*FullValue, error) {
			fn.Call2x0(n.args[0], n.args[1])
			return nil, nil
		}

	case reflectx.Func3x0:
		n.call = func(pn typex.PaneInfo, ws []typex.Window, ts typex.EventTime) (*FullValue, error) {
			fn.Call3x0(n.args[0], n.args[1], n.args[2])
			return nil, nil
		}

	case reflectx.Func4x0:
		n.call = func(pn typex.PaneInfo, ws []typex.Window, ts typex.EventTime) (*FullValue, error) {
			fn.Call4x0(n.args[0], n.args[1], n.args[2], n.args[3])
			return nil, nil
		}

	case reflectx.Func5x0:
		n.call = func(pn typex.PaneInfo, ws []typex.Window, ts typex.EventTime) (*FullValue, error) {
			fn.Call5x0(n.args[0], n.args[1], n.args[2], n.args[3], n.args[4])
			return nil, nil
		}

	case reflectx.Func6x0:
		n.call = func(pn typex.PaneInfo, ws []typex.Window, ts typex.EventTime) (*FullValue, error) {
			fn.Call6x0(n.args[0], n.args[1], n.args[2], n.args[3], n.args[4], n.args[5])
			return nil, nil
		}

	case reflectx.Func7x0:
		n.call = func(pn typex.PaneInfo, ws []typex.Window, ts typex.EventTime) (*FullValue, error) {
			fn.Call7x0(n.args[0], n.args[1], n.args[2], n.args[3], n.args[4], n.args[5], n.args[6])
			return nil, nil
		}

	case reflectx.Func8x0:
		n.call = func(pn typex.PaneInfo, ws []typex.Window, ts typex.EventTime) (*FullValue, error) {
			fn.Call8x0(n.args[0], n.args[1], n.args[2], n.args[3], n.args[4], n.args[5], n.args[6], n.args[7])
			return nil, nil
		}

	case reflectx.Func0x1:
		n.call = func(pn typex.PaneInfo, ws []typex.Window, ts typex.EventTime) (*FullValue, error) {
			r0 := fn.Call0x1()
			return n.ret1(pn, ws, ts, r0)
		}

	case reflectx.Func1x1:
		n.call = func(pn typex.PaneInfo, ws []typex.Window, ts typex.EventTime) (*FullValue, error) {
			r0 := fn.Call1x1(n.args[0])
			return n.ret1(pn, ws, ts, r0)
		}

	case reflectx.Func2x1:
		n.call = func(pn typex.PaneInfo, ws []typex.Window, ts typex.EventTime) (*FullValue, error) {
			r0 := fn.Call2x1(n.args[0], n.args[1])
			return n.ret1(pn, ws, ts, r0)
		}

	case reflectx.Func3x1:
		n.call = func(pn typex.PaneInfo, ws []typex.Window, ts typex.EventTime) (*FullValue, error) {
			r0 := fn.Call3x1(n.args[0], n.args[1], n.args[2])
			return n.ret1(pn, ws, ts, r0)
		}

	case reflectx.Func4x1:
		n.call = func(pn typex.PaneInfo, ws []typex.Window, ts typex.EventTime) (*FullValue, error) {
			r0 := fn.Call4x1(n.args[0], n.args[1], n.args[2], n.args[3])
			return n.ret1(pn, ws, ts, r0)
		}

	case reflectx.Func5x1:
		n.call = func(pn typex.PaneInfo, ws []typex.Window, ts typex.EventTime) (*FullValue, error) {
			r0 := fn.Call5x1(n.args[0], n.args[1], n.args[2], n.args[3], n.args[4])
			return n.ret1(pn, ws, ts, r0)
		}

	case reflectx.Func6x1:
		n.call = func(pn typex.PaneInfo, ws []typex.Window, ts typex.EventTime) (*FullValue, error) {
			r0 := fn.Call6x1(n.args[0], n.args[1], n.args[2], n.args[3], n.args[4], n.args[5])
			return n.ret1(pn, ws, ts, r0)
		}

	case reflectx.Func7x1:
		n.call = func(pn typex.PaneInfo, ws []typex.Window, ts typex.EventTime) (*FullValue, error) {
			r0 := fn.Call7x1(n.args[0], n.args[1], n.args[2], n.args[3], n.args[4], n.args[5], n.args[6])
			return n.ret1(pn, ws, ts, r0)
		}

	case reflectx.Func8x1:
		n.call = func(pn typex.PaneInfo, ws []typex.Window, ts typex.EventTime) (*FullValue, error) {
			r0 := fn.Call8x1(n.args[0], n.args[1], n.args[2], n.args[3], n.args[4], n.args[5], n.args[6], n.args[7])
			return n.ret1(pn, ws, ts, r0)
		}

	case reflectx.Func0x2:
		n.call = func(pn typex.PaneInfo, ws []typex.Window, ts typex.EventTime) (*FullValue, error) {
			r0, r1 := fn.Call0x2()
			return n.ret2(pn, ws, ts, r0, r1)
		}

	case reflectx.Func1x2:
		n.call = func(pn typex.PaneInfo, ws []typex.Window, ts typex.EventTime) (*FullValue, error) {
			r0, r1 := fn.Call1x2(n.args[0])
			return n.ret2(pn, ws, ts, r0, r1)
		}

	case reflectx.Func2x2:
		n.call = func(pn typex.PaneInfo, ws []typex.Window, ts typex.EventTime) (*FullValue, error) {
			r0, r1 := fn.Call2x2(n.args[0], n.args[1])
			return n.ret2(pn, ws, ts, r0, r1)
		}

	case reflectx.Func3x2:
		n.call = func(pn typex.PaneInfo, ws []typex.Window, ts typex.EventTime) (*FullValue, error) {
			r0, r1 := fn.Call3x2(n.args[0], n.args[1], n.args[2])
			return n.ret2(pn, ws, ts, r0, r1)
		}

	case reflectx.Func4x2:
		n.call = func(pn typex.PaneInfo, ws []typex.Window, ts typex.EventTime) (*FullValue, error) {
			r0, r1 := fn.Call4x2(n.args[0], n.args[1], n.args[2], n.args[3])
			return n.ret2(pn, ws, ts, r0, r1)
		}

	case reflectx.Func5x2:
		n.call = func(pn typex.PaneInfo, ws []typex.Window, ts typex.EventTime) (*FullValue, error) {
			r0, r1 := fn.Call5x2(n.args[0], n.args[1], n.args[2], n.args[3], n.args[4])
			return n.ret2(pn, ws, ts, r0, r1)
		}

	case reflectx.Func6x2:
		n.call = func(pn typex.PaneInfo, ws []typex.Window, ts typex.EventTime) (*FullValue, error) {
			r0, r1 := fn.Call6x2(n.args[0], n.args[1], n.args[2], n.args[3], n.args[4], n.args[5])
			return n.ret2(pn, ws, ts, r0, r1)
		}

	case reflectx.Func7x2:
		n.call = func(pn typex.PaneInfo, ws []typex.Window, ts typex.EventTime) (*FullValue, error) {
			r0, r1 := fn.Call7x2(n.args[0], n.args[1], n.args[2], n.args[3], n.args[4], n.args[5], n.args[6])
			return n.ret2(pn, ws, ts, r0, r1)
		}

	case reflectx.Func8x2:
		n.call = func(pn typex.PaneInfo, ws []typex.Window, ts typex.EventTime) (*FullValue, error) {
			r0, r1 := fn.Call8x2(n.args[0], n.args[1], n.args[2], n.args[3], n.args[4], n.args[5], n.args[6], n.args[7])
			return n.ret2(pn, ws, ts, r0, r1)
		}

	case reflectx.Func0x3:
		n.call = func(pn typex.PaneInfo, ws []typex.Window, ts typex.EventTime) (*FullValue, error) {
			r0, r1, r2 := fn.Call0x3()
			return n.ret3(pn, ws, ts, r0, r1, r2)
		}

	case reflectx.Func1x3:
		n.call = func(pn typex.PaneInfo, ws []typex.Window, ts typex.EventTime) (*FullValue, error) {
			r0, r1, r2 := fn.Call1x3(n.args[0])
			return n.ret3(pn, ws, ts, r0, r1, r2)
		}

	case reflectx.Func2x3:
		n.call = func(pn typex.PaneInfo, ws []typex.Window, ts typex.EventTime) (*FullValue, error) {
			r0, r1, r2 := fn.Call2x3(n.args[0], n.args[1])
			return n.ret3(pn, ws, ts, r0, r1, r2)
		}

	case reflectx.Func3x3:
		n.call = func(pn typex.PaneInfo, ws []typex.Window, ts typex.EventTime) (*FullValue, error) {
			r0, r1, r2 := fn.Call3x3(n.args[0], n.args[1], n.args[2])
			return n.ret3(pn, ws, ts, r0, r1, r2)
		}

	case reflectx.Func4x3:
		n.call = func(pn typex.PaneInfo, ws []typex.Window, ts typex.EventTime) (*FullValue, error) {
			r0, r1, r2 := fn.Call4x3(n.args[0], n.args[1], n.args[2], n.args[3])
			return n.ret3(pn, ws, ts, r0, r1, r2)
		}

	case reflectx.Func5x3:
		n.call = func(pn typex.PaneInfo, ws []typex.Window, ts typex.EventTime) (*FullValue, error) {
			r0, r1, r2 := fn.Call5x3(n.args[0], n.args[1], n.args[2], n.args[3], n.args[4])
			return n.ret3(pn, ws, ts, r0, r1, r2)
		}

	case reflectx.Func6x3:
		n.call = func(pn typex.PaneInfo, ws []typex.Window, ts typex.EventTime) (*FullValue, error) {
			r0, r1, r2 := fn.Call6x3(n.args[0], n.args[1], n.args[2], n.args[3], n.args[4], n.args[5])
			return n.ret3(pn, ws, ts, r0, r1, r2)
		}

	case reflectx.Func7x3:
		n.call = func(pn typex.PaneInfo, ws []typex.Window, ts typex.EventTime) (*FullValue, error) {
			r0, r1, r2 := fn.Call7x3(n.args[0], n.args[1], n.args[2], n.args[3], n.args[4], n.args[5], n.args[6])
			return n.ret3(pn, ws, ts, r0, r1, r2)
		}

	case reflectx.Func8x3:
		n.call = func(pn typex.PaneInfo, ws []typex.Window, ts typex.EventTime) (*FullValue, error) {
			r0, r1, r2 := fn.Call8x3(n.args[0], n.args[1], n.args[2], n.args[3], n.args[4], n.args[5], n.args[6], n.args[7])
			return n.ret3(pn, ws, ts, r0, r1, r2)
		}

	case reflectx.Func0x4:
		n.call = func(pn typex.PaneInfo, ws []typex.Window, ts typex.EventTime) (*FullValue, error) {
			r0, r1, r2, r3 := fn.Call0x4()
			return n.ret4(pn, ws, ts, r0, r1, r2, r3)
		}

	case reflectx.Func1x4:
		n.call = func(pn typex.PaneInfo, ws []typex.Window, ts typex.EventTime) (*FullValue, error) {
			r0, r1, r2, r3 := fn.Call1x4(n.args[0])
			return n.ret4(pn, ws, ts, r0, r1, r2, r3)
		}

	case reflectx.Func2x4:
		n.call = func(pn typex.PaneInfo, ws []typex.Window, ts typex.EventTime) (*FullValue, error) {
			r0, r1, r2, r3 := fn.Call2x4(n.args[0], n.args[1])
			return n.ret4(pn, ws, ts, r0, r1, r2, r3)
		}

	case reflectx.Func3x4:
		n.call = func(pn typex.PaneInfo, ws []typex.Window, ts typex.EventTime) (*FullValue, error) {
			r0, r1, r2, r3 := fn.Call3x4(n.args[0], n.args[1], n.args[2])
			return n.ret4(pn, ws, ts, r0, r1, r2, r3)
		}

	case reflectx.Func4x4:
		n.call = func(pn typex.PaneInfo, ws []typex.Window, ts typex.EventTime) (*FullValue, error) {
			r0, r1, r2, r3 := fn.Call4x4(n.args[0], n.args[1], n.args[2], n.args[3])
			return n.ret4(pn, ws, ts, r0, r1, r2, r3)
		}

	case reflectx.Func5x4:
		n.call = func(pn typex.PaneInfo, ws []typex.Window, ts typex.EventTime) (*FullValue, error) {
			r0, r1, r2, r3 := fn.Call5x4(n.args[0], n.args[1], n.args[2], n.args[3], n.args[4])
			return n.ret4(pn, ws, ts, r0, r1, r2, r3)
		}

	case reflectx.Func6x4:
		n.call = func(pn typex.PaneInfo, ws []typex.Window, ts typex.EventTime) (*FullValue, error) {
			r0, r1, r2, r3 := fn.Call6x4(n.args[0], n.args[1], n.args[2], n.args[3], n.args[4], n.args[5])
			return n.ret4(pn, ws, ts, r0, r1, r2, r3)
		}

	case reflectx.Func7x4:
		n.call = func(pn typex.PaneInfo, ws []typex.Window, ts typex.EventTime) (*FullValue, error) {
			r0, r1, r2, r3 := fn.Call7x4(n.args[0], n.args[1], n.args[2], n.args[3], n.args[4], n.args[5], n.args[6])
			return n.ret4(pn, ws, ts, r0, r1, r2, r3)
		}

	case reflectx.Func8x4:
		n.call = func(pn typex.PaneInfo, ws []typex.Window, ts typex.EventTime) (*FullValue, error) {
			r0, r1, r2, r3 := fn.Call8x4(n.args[0], n.args[1], n.args[2], n.args[3], n.args[4], n.args[5], n.args[6], n.args[7])
			return n.ret4(pn, ws, ts, r0, r1, r2, r3)
		}

	default:
		n.call = func(pn typex.PaneInfo, ws []typex.Window, ts typex.EventTime) (*FullValue, error) {
			ret := n.fn.Fn.Call(n.args)

			// (5) Return direct output, if any. Input timestamp and windows are implicitly
			// propagated.
			switch len(ret) {
			case 0:
				return nil, nil
			case 1:
				return n.ret1(pn, ws, ts, ret[0])
			case 2:
				return n.ret2(pn, ws, ts, ret[0], ret[1])
			case 3:
				return n.ret3(pn, ws, ts, ret[0], ret[1], ret[2])
			case 4:
				return n.ret4(pn, ws, ts, ret[0], ret[1], ret[2], ret[3])
			case 5:
				return n.ret5(pn, ws, ts, ret[0], ret[1], ret[2], ret[3], ret[4])
			}
			panic(fmt.Sprintf("invoker: %v has > 5 return values, which is not permitted", n.fn.Fn.Name()))
		}
	}
}
