let queue = [];
function getContext() {
    const pages = getCurrentPages();
    return pages[pages.length - 1];
}
const Dialog = options => {
    options = Object.assign(Object.assign({}, Dialog.currentOptions), options);
    return new Promise((resolve, reject) => {
        const context = options.context || getContext();
        const dialog = context.selectComponent(options.selector);
        delete options.context;
        delete options.selector;
        if (dialog) {
            dialog.setData(Object.assign({ onCancel: reject, onConfirm: resolve }, options));
            queue.push(dialog);
        }
        else {
            console.warn('未找到 van-dialog 节点，请确认 selector 及 context 是否正确');
        }
    });
};
Dialog.defaultOptions = {
    show: true,
    title: '',
    width: null,
    message: '',
    zIndex: 100,
    overlay: true,
    selector: '#van-dialog',
    className: '',
    asyncClose: false,
    transition: 'scale',
    customStyle: '',
    messageAlign: '',
    overlayStyle: '',
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    showConfirmButton: true,
    showCancelButton: false,
    closeOnClickOverlay: false,
    confirmButtonOpenType: ''
};
Dialog.alert = Dialog;
Dialog.confirm = options => Dialog(Object.assign({ showCancelButton: true }, options));
Dialog.close = () => {
    queue.forEach(dialog => {
        dialog.close();
    });
    queue = [];
};
Dialog.stopLoading = () => {
    queue.forEach(dialog => {
        dialog.stopLoading();
    });
};
Dialog.setDefaultOptions = options => {
    Object.assign(Dialog.currentOptions, options);
};
Dialog.resetDefaultOptions = () => {
    Dialog.currentOptions = Object.assign({}, Dialog.defaultOptions);
};
Dialog.resetDefaultOptions();
export default Dialog;
