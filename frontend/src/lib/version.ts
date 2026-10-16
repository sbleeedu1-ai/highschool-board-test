/** 화면 하단에 보여 줄 버전 문구. 커밋 번호는 길어서 앞 7자리만 쓴다. */
export function versionLabel(version: string | null): string {
  if (version === null) return '버전 확인 불가'
  return `버전 ${version.slice(0, 7)}`
}

/** 백엔드에 배포된 버전을 묻는다. 실패하면 null. */
export async function fetchVersion(): Promise<string | null> {
  try {
    const res = await fetch('/api/version')
    if (!res.ok) return null
    const body: { version: string } = await res.json()
    return body.version
  } catch {
    return null
  }
}
