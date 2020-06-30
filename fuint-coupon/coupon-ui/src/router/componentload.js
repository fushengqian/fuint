export function _load(file) {
  return () => {
    import ('@/page/' + file + '.vue')
  }
}