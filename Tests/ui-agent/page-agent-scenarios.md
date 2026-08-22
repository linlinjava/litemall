# Page Agent UI Layer Scenarios

This layer adds Page Agent as an optional AI Native UI exploration tool for litemall payment and order flows. It is not part of `./Tests/scripts/run-layered-tests.sh all` because it depends on an LLM key, external model behavior, and browser-resident automation.

## Why It Fits The UI Layer

Page Agent runs inside the webpage. Its `PageAgent` class combines an agent loop, DOM controller, and optional panel. The loop observes simplified DOM text, asks an OpenAI-compatible LLM for the next action, executes tools such as click, input, select, scroll, wait, and done, then returns history evidence.

Use it to discover or repair UI flows, then convert stable traces into Playwright tests.

## Local Setup

Load Page Agent only in local development or a training harness:

```html
<script src="https://cdn.jsdelivr.net/npm/page-agent@1.11.0/dist/iife/page-agent.demo.js?autoInit=false"></script>
```

Then create an agent from the browser console or an internal local-only helper:

```js
const agent = new window.PageAgent({
  model: window.sessionStorage.getItem('PAGE_AGENT_MODEL'),
  baseURL: window.sessionStorage.getItem('PAGE_AGENT_BASE_URL'),
  apiKey: window.sessionStorage.getItem('PAGE_AGENT_API_KEY'),
  language: 'zh-CN',
  maxSteps: 16,
  transformPageContent: (content) =>
    content
      .replace(/1[3-9]\d{9}/g, '***********')
      .replace(/sk-[A-Za-z0-9_-]+/g, 'sk-***')
})
```

Use a training-only key and clear session storage after the run. Do not commit real keys. Do not run this against production customer or payment data.

## Output Evidence Contract

```json
{
  "test_case_id": "MALL-PAY-AC08-UIAGENT-001",
  "target_url": "http://localhost:8080/#/checkout",
  "prompt": "使用测试用户提交订单并完成支付，确认订单最终只进入一次已支付状态。",
  "model": "env:PAGE_AGENT_MODEL",
  "seed": "local payment harness seeded order/cart/coupon",
  "result_success": true,
  "history_summary": [
    { "step": 0, "action": "click/input/scroll", "business_evidence": "..." }
  ],
  "candidate_playwright_steps": [
    "await page.getByRole('button', { name: '提交订单' }).click()"
  ],
  "decision": "convert-to-playwright"
}
```

## Scenario Pack

| test_case_id | AC | Prompt | Required Evidence | Handoff |
| --- | --- | --- | --- | --- |
| MALL-PAY-AC08-UIAGENT-001 | AC08 | 使用测试用户提交订单并完成支付，确认订单最终只进入一次已支付状态。 | 订单状态可见为 paid/已付款；重复回调计数仍为 1。 | Convert successful trace to `e2e/tests/payment-flow.spec.ts` steps. |
| MALL-PAY-AC09-UIAGENT-001 | AC09 | 创建未支付订单后取消订单，确认库存和优惠券恢复。 | 页面显示订单取消；库存恢复；优惠券可再次使用。 | Convert only visible UI path to Playwright; keep stock/coupon DB proof in backend IT. |
| MALL-PAY-AC12-UIAGENT-001 | AC12 | 对已付款未发货订单发起退款申请，确认订单进入退款中。 | 页面出现退款中状态或退款申请成功提示。 | Convert to Playwright smoke only after backend UT keeps refund state rules. |

## Review Rules

- Page Agent output is exploratory evidence, not a passed E2E gate.
- Preserve `test_case_id` in prompt, trace file, generated script, and review note.
- Convert DOM indexes into stable Playwright selectors before accepting a script.
- Keep `experimentalScriptExecutionTool` disabled unless a security review approves local-only JavaScript execution.
- If Page Agent discovers a UI ambiguity, add a review finding instead of weakening assertions.
- If a trace is flaky or model-dependent, mark it `manual-evidence` and do not promote it to CI.
