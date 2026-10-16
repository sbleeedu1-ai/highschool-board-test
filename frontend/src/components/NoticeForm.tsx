import { useState, type FormEvent } from 'react'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Textarea } from '@/components/ui/textarea'
import { ApiError } from '@/lib/api'
import { BODY_MAX, TITLE_MAX, countLabel, validateNotice, type FieldErrors } from '@/lib/noticeInput'

export type NoticeFormValues = { title: string; body: string }

type Props = {
  heading: string
  submitLabel: string
  initial?: NoticeFormValues
  /** 서버에 보낸다. 실패하면 ApiError를 던진다(401은 sendJson이 로그인 화면으로 보낸다) */
  onSubmit: (values: NoticeFormValues) => Promise<void>
}

/** 공지 등록·수정 화면이 함께 쓰는 입력 칸. 글자 수와 칸별 안내를 보여 준다 */
export default function NoticeForm({ heading, submitLabel, initial, onSubmit }: Props) {
  const [values, setValues] = useState<NoticeFormValues>(initial ?? { title: '', body: '' })
  // 한 번이라도 고친 칸(또는 등록을 누른 뒤의 모든 칸)만 안내를 보여 준다. 처음부터 빨간 글씨가 뜨지 않게
  const [touched, setTouched] = useState({ title: false, body: false })
  const [serverErrors, setServerErrors] = useState<FieldErrors>({})
  const [formError, setFormError] = useState<string | null>(null)
  const [submitting, setSubmitting] = useState(false)

  const clientErrors = validateNotice(values.title, values.body)
  const errorOf = (field: 'title' | 'body') => serverErrors[field] ?? (touched[field] ? clientErrors[field] : undefined)

  function change(field: 'title' | 'body', value: string) {
    setValues((v) => ({ ...v, [field]: value }))
    setTouched((t) => ({ ...t, [field]: true }))
    setServerErrors((e) => ({ ...e, [field]: undefined }))
  }

  async function submit(e: FormEvent) {
    e.preventDefault()
    setTouched({ title: true, body: true })
    setFormError(null)
    if (Object.keys(clientErrors).length > 0) return

    setSubmitting(true)
    try {
      await onSubmit(values)
    } catch (err) {
      if (err instanceof ApiError && err.status === 400 && err.fieldErrors.length > 0) {
        setServerErrors(Object.fromEntries(err.fieldErrors.map((f) => [f.field, f.message])))
      } else if (err instanceof ApiError && err.status === 403) {
        setFormError('다시 시도해 주세요')
      } else if (!(err instanceof ApiError && err.status === 401)) {
        setFormError('잠시 후 다시 시도해 주세요')
      }
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <form onSubmit={submit} noValidate className="flex flex-col gap-5">
      <h2 className="text-xl font-semibold">{heading}</h2>
      <Field id="title" label="제목" count={countLabel(values.title, TITLE_MAX)} error={errorOf('title')}>
        <Input
          id="title"
          value={values.title}
          onChange={(e) => change('title', e.target.value)}
          aria-invalid={errorOf('title') ? true : undefined}
        />
      </Field>
      <Field id="body" label="본문" count={countLabel(values.body, BODY_MAX)} error={errorOf('body')}>
        <Textarea
          id="body"
          rows={12}
          value={values.body}
          onChange={(e) => change('body', e.target.value)}
          aria-invalid={errorOf('body') ? true : undefined}
        />
      </Field>
      {formError && (
        <p role="alert" className="text-sm text-destructive">
          {formError}
        </p>
      )}
      <div>
        <Button type="submit" disabled={submitting}>
          {submitLabel}
        </Button>
      </div>
    </form>
  )
}

function Field(props: { id: string; label: string; count: string; error?: string; children: React.ReactNode }) {
  return (
    <div className="flex flex-col gap-2">
      <div className="flex items-baseline justify-between">
        <Label htmlFor={props.id}>{props.label}</Label>
        <span className="text-xs text-muted-foreground">{props.count}</span>
      </div>
      {props.children}
      {props.error && <p className="text-sm text-destructive">{props.error}</p>}
    </div>
  )
}
